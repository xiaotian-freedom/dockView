package com.storn.dockview

import android.graphics.drawable.Drawable

/**
 * @Description:
 * @Author: TST
 * @CreateDate: 2021/6/29$ 11:08 下午$
 * @UpdateUser:
 * @UpdateDate: 2021/6/29$ 11:08 下午$
 * @UpdateRemark:
 * @Version: 1.0
 */
interface OnImageLoadListener {
    fun onSuccess(resource: Drawable)
    fun onFailed()
}