package com.xdja.imp.data.cache;

import com.xdja.imp.data.persistent.PropertyUtil;

import javax.inject.Inject;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.data.cache</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/10</p>
 * <p>Time:20:22</p>
 */
public class CardCacheImp implements CardCache {

    private PropertyUtil util;

    @Inject
    public CardCacheImp(PropertyUtil util){
        this.util = util;
    }

    private CardEntity cardEntity;

    @Override
    public void put(CardEntity cardEntity) {
        this.cardEntity = cardEntity;
    }

    @Override
    public CardEntity get() {
        final String PRONAME = "config.properties";// modified by ycm for lint 2017/02/16
        final String TAG_CARDID = "cardId";
        if (this.cardEntity == null) {
            this.util.load(PRONAME);
            this.cardEntity = new CardEntity();
            this.cardEntity.setCardId(this.util.get(TAG_CARDID));
        }

        return this.cardEntity;
    }
}
