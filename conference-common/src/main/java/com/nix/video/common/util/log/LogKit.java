package com.nix.video.common.util.log;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author keray
 */
public final class LogKit {
    private static Logger log = LoggerFactory.getLogger("nix");

    private static String getClassName(Class clazz){
        return clazz.getName() + " : ";
    }

    public static void info(Class clazz,String msg){
        info("{} : {}",getClassName(clazz),msg);
    }

    public static void info(String msg) {
        log.info(msg);
    }

    public static void info(String tem,Object ... param) {
        log.info(tem,param);
    }
    public static void info(Class clazz,String tem,Object ... param){
        info("{} : " + tem,getClassName(clazz),param);
    }

    public static void debug(Class clazz,String msg){
        debug("{} : {}",getClassName(clazz),msg);
    }

    public static void debug(String msg) {
        log.debug(msg);
    }

    public static void debug(String tem,Object ... param) {
        log.debug(tem, param);
    }
    public static void debug(Class clazz,String tem,Object ... param){
        debug("{} : " + tem,getClassName(clazz),param);
    }

    public static void warn(Class clazz,String msg){
        warn("{} : {}",getClassName(clazz),msg);
    }

    public static void warn(String msg) {
        log.warn(msg);
    }

    public static void warn(String tem,Object ... param) {
        log.warn(tem,param);
    }
    public static void warn(Class clazz,String tem,Object ... param){
        warn("{} : " + tem,getClassName(clazz),param);
    }



    public static void error(String tem,Object ...param){
        log.error(tem,param);
    }
    public static void error(String msg,Exception e){
        log.error(msg,e);
    }
    public static void error(String msg){
        log.error(msg);
    }

    public static void error(Class clazz,String tem,Object ...param){
        error("{}:" + tem,getClassName(clazz),param);
    }
    public static void error(Class clazz,String msg,Exception e){
        error(getClassName(clazz) + ":" + msg,e);
    }
    public static void error(Class clazz,String msg){
        error(clazz,msg,"");
    }
}