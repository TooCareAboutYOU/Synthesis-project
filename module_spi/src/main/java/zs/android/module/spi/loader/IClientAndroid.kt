package zs.android.module.spi.loader

import android.util.Log

/**
 * @author zhangshuai@attrsense.com
 * @date 2023/2/6 17:51
 * @description
 */
class IClientAndroid : IClient {
    override fun onEvent() {
        Log.d("print_logs", "IClientAndroid::onEvent: ")
    }
}