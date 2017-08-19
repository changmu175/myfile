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
 * Created by ldy on 16/4/21.
 * bitmap压缩成原图和缩略图的jpg文件并上传至文件服务器用例
 */
public class UploadImgUseCase extends Ext1UseCase<Bitmap,Map<String,String>> {
    private final UserInfoRepository.PreUserInfoRepository userInfoRepository;
    public static final String AVATAR_ID = "avatarId";
    public static final String THUMBNAIL_ID = "thumbnailId";

    @Inject
    public UploadImgUseCase(ThreadExecutor threadExecutor,
                         PostExecutionThread postExecutionThread,
                         UserInfoRepository.PreUserInfoRepository userInfoRepository) {
        super(threadExecutor, postExecutionThread);
        this.userInfoRepository = userInfoRepository;
    }
    /**
     * 构建业务处理事件流
     *
     * @return 目标事件流
     */
    @Override
    public Observable<Map<String,String>> buildUseCaseObservable() {
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
                });
    }


}



