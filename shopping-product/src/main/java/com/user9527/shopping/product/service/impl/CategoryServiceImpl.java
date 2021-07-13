package com.user9527.shopping.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.user9527.common.utils.PageUtils;
import com.user9527.common.utils.Query;
import com.user9527.shopping.product.dao.CategoryDao;
import com.user9527.shopping.product.entity.CategoryEntity;
import com.user9527.shopping.product.service.CategoryBrandRelationService;
import com.user9527.shopping.product.service.CategoryService;
import com.user9527.shopping.product.vo.Catelog2VO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        //1 查出所有数据
        List<CategoryEntity> categoryEntityList = baseMapper.selectList(null);

        //2 把数据组装成父子的树形结构
        //2.1 找到所有的一级分类
        List<CategoryEntity> level1Menus = categoryEntityList.stream().filter(categoryEntity ->
                categoryEntity.getParentCid() == 0
        ).map(menu -> {
            menu.setChildren(getChildrens(menu, categoryEntityList));
            return menu;
        }).sorted((menu1, menu2) ->
                (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort())
        ).collect(Collectors.toList());

        return level1Menus;
    }

    @Override
    public void removeMenuByIds(List<Long> asList) {
        //TODO  1，检查当前删除的菜单，是否被其他地方引用 （逻辑删除）
        baseMapper.deleteBatchIds(asList);
    }

    @CacheEvict(value = {"category"}, allEntries = true)
    @Transactional
    @Override
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
    }

    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> paths = new ArrayList<>();
        List<Long> parentPath = findParentPath(catelogId, paths);
        Collections.reverse(parentPath);

        return parentPath.toArray(new Long[parentPath.size()]);
    }

    /**
     * 代表当前方法的结果需要缓存，如果缓存中有，方法不在调用。如果缓存中没有，会调用方法
     * 自定义缓存配置
     *      1) 将数据保存为json格式
     *      原理
     *      CacheAutoConfiguration -> RedisCacheConfiguration -> 自动配置了RedisCacheManager -> 初始化所有缓存
     *      -> 每个缓存决定使用什么配置 -> 如果redisCacheConfiguration有就用已有的，没有就用默认配置
     *      -> 想改缓存的配置，只需要给容器中放一个redisCacheConfiguration即可
     *      -> 就会应用到当前RedisCacheManager管理的所有缓存分区中
     * @Cacheable: Triggers cache population.
     * @CacheEvict: Triggers cache eviction.
     * @CachePut: Updates the cache without interfering with the method execution.
     * @Caching: Regroups multiple cache operations to be applied on a method.
     * @CacheConfig: Shares some common cache-related settings at class-level.
     *  spring cache的不足
     *      1） 读模式
     *          缓存穿透：查询一个null数据。解决：缓存空数据；spring.cache.redis.cache-null-values
     *          缓存击穿：大量并发进来同时查询一个正好过期的数据；解决：加锁； 默认是无加锁的 sync = true
     *          缓存雪崩：大量key同时过期；解决：加随机时间。加上过期时间：spring.cache.redis.time-to-live
     *      2） 写模式
     *          读写加锁
     *          引入canal
     *          读多写多，直接去数据库查询就行
     *      总结：
     *          常规数据（读多写少，及时性，一致性要求不高的数据）；完全可以使用spring cache
     *
     *
     * @return
     */
    @Cacheable(value = {"category"}, key = "#root.methodName", sync = true)
    @Override
    public List<CategoryEntity> getLevel1Categorys() {
        System.out.println("一级菜单...");
        List<CategoryEntity> entities = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
        return entities;
    }

    @Cacheable(value = {"category"}, key = "#root.methodName", sync = true)
    @Override
    public Map<String, List<Catelog2VO>> getCatelogJson() {
        List<CategoryEntity> selectList = baseMapper.selectList(null);

        // 1,查出所有一级分类
        List<CategoryEntity> level1Categorys = getParent_cid(selectList, 0L);
        // 2,封装数据
        Map<String, List<Catelog2VO>> parent_cid = level1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            // 每一个一级分类，查到这个一级分类的所有二级分类
            List<CategoryEntity> level2Categorys = getParent_cid(selectList, v.getCatId());
            List<Catelog2VO> catelog2VOS = null;
            if (!CollectionUtils.isEmpty(level2Categorys)) {
                // 封装二级分类
                catelog2VOS = level2Categorys.stream().map(l2 -> {
                    Catelog2VO catelog2VO = new Catelog2VO(v.getCatId().toString(), l2.getCatId().toString(), l2.getName(), null);
                    List<CategoryEntity> level3Categorys = getParent_cid(selectList, l2.getCatId());
                    // 封装三级分类
                    if (!CollectionUtils.isEmpty(level3Categorys)) {
                        List<Catelog2VO.Catelog3VO> catelog3VOS = level3Categorys.stream().map(l3 -> {
                            Catelog2VO.Catelog3VO catelog3VO = new Catelog2VO.Catelog3VO(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());
                            return catelog3VO;
                        }).collect(Collectors.toList());
                        catelog2VO.setCatalog3List(catelog3VOS);
                    }
                    return catelog2VO;
                }).collect(Collectors.toList());

            }
            return catelog2VOS;
        }));
        return parent_cid;
    }
    /**
     * TODO 产生堆外内存溢出 OutOfDirectMemoryError
     * springboot 2.0 以后默认使用 lettuce 作为操作 redis 的客户端，它使用 netty 进行通信
     * lettuce 的 bug 导致 netty 堆外内存溢出
     * -Xmx300m：如果没有指定堆外内存，netty 默认使用 堆内存（Xmx） 作为 堆外内存
     * 升级 lettuce 客户端
     * 使用 Jedis（推荐）
     *
     * @return
     */
//    @Override
    public Map<String, List<Catelog2VO>> getCatelogJson2() {
        /**
         * 1，空结果缓存：解决缓存穿透
         * 2，设置过期时间（加随机值）：解决缓存雪崩
         * 3，加锁：解决缓存击穿
         */
        // 给缓存中放json字符串，拿出的json字符串，还能逆转为能用的对象类型；【序列化与反序列化】
        // 1，加入缓存逻辑，缓存中存的数据是json字符串
        // json跨语言，跨平台兼容
        String catelogJson = stringRedisTemplate.opsForValue().get("catelogJson");
        // 缓存里面没有数据  
        if (StringUtils.isEmpty(catelogJson)) {
            System.out.println("缓存中没有数据...查询数据库");
            // 2，缓存中没有，查询数据库
            Map<String, List<Catelog2VO>> catelogJsonFromDb = getCatelogJsonFromDbWithRedisLock();
            return catelogJsonFromDb;
        }
        System.out.println("缓存中有数据...");
        Map<String, List<Catelog2VO>> result = JSON.parseObject(catelogJson, new TypeReference<Map<String, List<Catelog2VO>>>() {
        });
        return result;
    }

    public Map<String, List<Catelog2VO>> getCatelogJsonFromDbWithRedisLock() {
        // 1，占分布式锁。去redis占坑
        String uuid = UUID.randomUUID().toString();
        Boolean lock = stringRedisTemplate.opsForValue().setIfAbsent("lock", uuid, 300, TimeUnit.SECONDS);
        if(lock){
            System.out.println("获取分布式锁成功。。。");
            // 加锁成功...执行业务
            // 2，设置过期时间，必须和加锁是同步的，原子的
            Map<String, List<Catelog2VO>> dataFromDb;
            try {
                dataFromDb = getDataFromDb();
            }finally {
                String script = "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
                Long execute = stringRedisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class)
                        , Arrays.asList("lock")
                        , uuid);
                stringRedisTemplate.delete("lock");
            }

            // 删除锁
            // 获取值对比 + 对比成功 = 原子的  lua脚本解锁
//            String lockValue = stringRedisTemplate.opsForValue().get("lock");
//            if(uuid.equalsIgnoreCase(lockValue)){
//                // 删除自己的锁
//                stringRedisTemplate.delete("lock");
//            }

            return dataFromDb;
        }else {
            // 加锁失败...重试。
            System.out.println("获取分布式锁失败。。。");
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return getCatelogJsonFromDbWithRedisLock();
        }
    }

    private Map<String, List<Catelog2VO>> getDataFromDb() {
        // 得到锁以后，我们应该再去缓存中确认一次，如果没有才需要查询
        String catelogJson = stringRedisTemplate.opsForValue().get("catelogJson");
        if (!StringUtils.isEmpty(catelogJson)) {
            Map<String, List<Catelog2VO>> result = JSON.parseObject(catelogJson, new TypeReference<Map<String, List<Catelog2VO>>>() {
            });
            return result;
        }
        System.out.println("查询数据库...");
        /**
         * 1 减少与数据库交互，将数据库的多次查询变为一次
         */
        List<CategoryEntity> selectList = baseMapper.selectList(null);

        // 1,查出所有一级分类
        List<CategoryEntity> level1Categorys = getParent_cid(selectList, 0L);
        // 2,封装数据
        Map<String, List<Catelog2VO>> parent_cid = level1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            // 每一个一级分类，查到这个一级分类的所有二级分类
            List<CategoryEntity> level2Categorys = getParent_cid(selectList, v.getCatId());
            List<Catelog2VO> catelog2VOS = null;
            if (!CollectionUtils.isEmpty(level2Categorys)) {
                // 封装二级分类
                catelog2VOS = level2Categorys.stream().map(l2 -> {
                    Catelog2VO catelog2VO = new Catelog2VO(v.getCatId().toString(), l2.getCatId().toString(), l2.getName(), null);
                    List<CategoryEntity> level3Categorys = getParent_cid(selectList, l2.getCatId());
                    // 封装三级分类
                    if (!CollectionUtils.isEmpty(level3Categorys)) {
                        List<Catelog2VO.Catelog3VO> catelog3VOS = level3Categorys.stream().map(l3 -> {
                            Catelog2VO.Catelog3VO catelog3VO = new Catelog2VO.Catelog3VO(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());
                            return catelog3VO;
                        }).collect(Collectors.toList());
                        catelog2VO.setCatalog3List(catelog3VOS);
                    }
                    return catelog2VO;
                }).collect(Collectors.toList());

            }
            return catelog2VOS;
        }));
        // 转成JSON字符串放在缓存中
        String s = JSON.toJSONString(parent_cid);
        stringRedisTemplate.opsForValue().set("catelogJson", s, 1, TimeUnit.DAYS);
        return parent_cid;
    }

    /**
     * 从数据库获取数据
     *
     * @return
     */
    public Map<String, List<Catelog2VO>> getCatelogJsonFromDbWithLocalLock() {

        // 只要是同一把锁，就能锁住需要这个锁的所有线程
        // synchronized (this)：springboot所有组件在容器中都是单例的
        synchronized (this) {
            // 得到锁以后，我们应该再去缓存中确认一次，如果没有才需要查询
            return getDataFromDb();
        }
    }

    private List<CategoryEntity> getParent_cid(List<CategoryEntity> selectList, Long parent_cid) {
        List<CategoryEntity> entityList = selectList.stream().filter(item -> item.getParentCid().equals(parent_cid)).collect(Collectors.toList());
        return entityList;
    }

    private List<Long> findParentPath(Long catelogId, List<Long> paths) {
        // 收集当前节点
        paths.add(catelogId);
        CategoryEntity byId = this.getById(catelogId);
        if (byId.getParentCid() != 0) {
            findParentPath(byId.getParentCid(), paths);
        }
        return paths;
    }

    //递归查找所有菜单的子菜单
    private List<CategoryEntity> getChildrens(CategoryEntity root, List<CategoryEntity> all) {
        List<CategoryEntity> collect = all.stream().filter(entity ->
                entity.getParentCid() == root.getCatId()
        ).map(categoryEntity -> {
            //找到子菜单
            categoryEntity.setChildren(getChildrens(categoryEntity, all));
            return categoryEntity;
        }).sorted((menu1, menu2) -> {
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
        }).collect(Collectors.toList());

        return collect;
    }

}