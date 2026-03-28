package com.ctx.course_service.utils.video;

import lombok.extern.slf4j.Slf4j;
import ws.schild.jave.EncoderException;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.info.MultimediaInfo;

import java.io.File;

@Slf4j
public class VideoUtil {
    public static double getVideoDuration(File file) throws EncoderException {
        try {
            MultimediaObject multimediaObject = new MultimediaObject(file);
            MultimediaInfo multimediaInfo = multimediaObject.getInfo();
            long duration = multimediaInfo.getDuration();
            return (duration /1000.0);
        }catch (Exception e){
            log.error(e.getMessage());
            throw e;
        }
    }
}
