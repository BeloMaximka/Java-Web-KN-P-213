package itstep.learning.rest;

public class RestResponse {
    private RestMetaData meta;
    private RestStatus status;
    private Object data;

    public RestMetaData getMeta() {
        return meta;
    }

    public void setMeta(RestMetaData meta) {
        this.meta = meta;
    }

    public RestStatus getStatus() {
        return status;
    }

    public void setStatus(RestStatus status) {
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
