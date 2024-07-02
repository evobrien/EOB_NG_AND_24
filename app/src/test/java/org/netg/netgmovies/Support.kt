package org.netg.netgmovies

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.single

/*
* This is based on the gist here:
* https://gist.github.com/Orgmir/05b4b0265ca63fed46f2c6496c9ad913
* the internal implementation of Paging data looks to have changed since
* the original post above was written - the changes below are what match
* the paging 3.3.0 version
* */
@Suppress("UNCHECKED_CAST")
suspend fun <T : Any> PagingData<T>.asList(): List<T> {
    val flow = PagingData::class.java.getDeclaredField("flow").apply {
        isAccessible = true
    }.get(this) as Flow<Any?>
    val pageEventInsert = flow.single()

    val transformablePageDataField =
        Class.forName("androidx.paging.PageEvent\$StaticList").getDeclaredField("data").apply {
            isAccessible = true
        }

    val listItems = transformablePageDataField.get(pageEventInsert)
    return listItems as List<T>
}
