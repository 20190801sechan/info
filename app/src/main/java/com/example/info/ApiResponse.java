package com.example.info;

import java.util.List;

public class ApiResponse {
    private Body body;

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public static class Body {
        private List<Medication> items;

        public List<Medication> getItems() {
            return items;
        }

        public void setItems(List<Medication> items) {
            this.items = items;
        }
    }
}

