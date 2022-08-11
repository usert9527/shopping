package com.user9527.shopping.product.web;

import java.util.List;

/**
 * @author yangtao
 * @version 1.0
 * @date 2021/11/21 18:42
 */
public class One {
    private int type;
    private int rate;
    private List<Attr> info;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public List<Attr> getInfo() {
        return info;
    }

    public void setInfo(List<Attr> info) {
        this.info = info;
    }

    @Override
    public String toString() {
        return "One{" +
                "type=" + type +
                ", rate=" + rate +
                ", info=" + info +
                '}';
    }

    class Attr{
        private int code;
        private String name;

        @Override
        public String toString() {
            return "Attr{" +
                    "code=" + code +
                    ", name='" + name + '\'' +
                    '}';
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
