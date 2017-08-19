package com.xdja.domain_mainframe.repository;

import rx.Observable;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.domain_mainframe.repository</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/5/3</p>
 * <p>Time:11:35</p>
 */
public interface ChipRepository {

    /**
     * 检测安全芯片驱动的存在状况
     *
     * @return 检测结果
     */
    Observable<Integer> checkDriverExist();

    /**
     * 检测芯片的存在状况
     *
     * @return 检测结果
     */
    Observable<Boolean> checkChipExist();

    /**
     * 判断安全芯片是否激活
     *
     * @return 判断结果
     */
    Observable<Boolean> isChipActived();

    /**
     * 激活芯片
     *
     * @return 激活结果
     */
    Observable<Boolean> activeChip();

    /**
     * 检测是否切换过安全卡
     *
     * @return 检测结果
     */
    Observable<Boolean> isChangedChip();

}
