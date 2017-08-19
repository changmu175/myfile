package com.xdja.domain_mainframe.usecase.userInfo;

import android.graphics.Bitmap;

import com.xdja.dependence.exeptions.CheckException;
import com.xdja.domain_mainframe.model.ImgCompressResult;
import com.xdja.domain_mainframe.repository.UserInfoRepository;
import com.xdja.frame.domain.excutor.PostExecutionThread;
import com.xdja.frame.domain.excutor.ThreadExecutor;
import com.xdja.frame.domain.usecase.Ext1UseCase;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;

/**
 * Created by ldy on 16/4/20.
 * 修改头像用例,
 * <p>1.先将传进来的bitmap压缩成两个jpg文件(原图和缩略图)</p>
 * <p>2.将两个文件分别上传到文件服务器上</p>
 * <p>3.将文件服务器分别传回的两个文件id用于修改头像</p>
 */
public class ModifyAvatarUseCase extends Ext1UseCase<Bitmap, Void> {

    private final UserInfoRepository.PreUserInfoRepository userInfoRepository;
    private final UserInfoRepository postUserInfoRepository;
    private static final String AVATAR_ID = "avatarId";
    private static final String THUMBNAIL_ID = "thumbnailId";

    @Inject
    public ModifyAvatarUseCase(ThreadExecutor threadExecutor,
                               PostExecutionThread postExecutionThread,
                               UserInfoRepository.PreUserInfoRepository userInfoRepository,
                               UserInfoRepository postUserInfoRepository) {
        super(threadExecutor, postExecutionThread);
        this.userInfoRepository = userInfoRepository;
        this.postUserInfoRepository = postUserInfoRepository;
    }

    /**
     * 构建业务处理事件流
     *
     * @return 目标事件流
     */
    @Override
    public Observable<Void> buildUseCaseObservable() {
        if (p == null) {
            return Observable.error(
                    new CheckException("bitmap cannot null")
            );
        }
        return userInfoRepository
                //压缩bitmap
                .compressBitmap2jpg(p)
                //分别上传压缩所得的原图和缩略图
                .concatMap(new Func1<ImgCompressResult, Observable<Map<String, String>>>() {
                    @Override
                    public Observable<Map<String, String>> call(ImgCompressResult imgCompressResult) {
                        //上传头像
                        return userInfoRepository.uploadImg(imgCompressResult.getImgFile())
                                .zipWith(
                                        userInfoRepository.uploadImg(imgCompressResult.getThumbnailImgFile()),
                                        new Func2<String, String, Map<String, String>>() {
                                            @Override
                                            public Map<String, String> call(String s, String s2) {
                                                Map<String, String> map = new HashMap<String, String>();
                                                map.put(AVATAR_ID, s);
                                                map.put(THUMBNAIL_ID, s2);
                                                return map;
                                            }
                                        });

                    }
                })
                .concatMap(new Func1<Map<String, String>, Observable<Void>>() {
                    @Override
                    public Observable<Void> call(Map<String, String> stringStringMap) {
                        //修改头像id
                        return postUserInfoRepository.modifyAvatar(
                                stringStringMap.get(AVATAR_ID),
                                stringStringMap.get(THUMBNAIL_ID));
                    }
                });

    }
}
