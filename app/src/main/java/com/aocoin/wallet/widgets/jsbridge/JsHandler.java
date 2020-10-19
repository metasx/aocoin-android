package com.aocoin.wallet.widgets.jsbridge;

public interface JsHandler<T,R> {
    void handler(T data, JsCallback<R> callback);
}