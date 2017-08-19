package com.xdja.contact.executor;

import com.xdja.contact.bean.dto.LocalCacheDto;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 搜索用线程池
 * @author hkb.
 * @since 2015/7/23/0023.
 */
public class SearchAsyncExecutor {
    public static ExecutorService SEARCH_TASK_POOL= Executors.newScheduledThreadPool(20);

    public static ArrayList<LocalCacheDto> cacheSearchResult = new ArrayList<>();

}
