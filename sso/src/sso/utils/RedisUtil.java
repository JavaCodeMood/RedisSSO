package sso.utils;

import java.sql.Connection;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public final class RedisUtil {
    
    //Redis������IP
    private static String ADDR = "127.0.0.1";
    
    //Redis�Ķ˿ں�
    private static int PORT = 6379;
    
    //��������
    private static String AUTH = "admin";
    
    //��������ʵ���������Ŀ��Ĭ��ֵΪ8��
    //�����ֵΪ-1�����ʾ�����ƣ����pool�Ѿ�������maxActive��jedisʵ�������ʱpool��״̬Ϊexhausted(�ľ�)��
    private static int MAX_ACTIVE = 1024;
    
    //����һ��pool����ж��ٸ�״̬Ϊidle(���е�)��jedisʵ����Ĭ��ֵҲ��8��
    private static int MAX_IDLE = 200;
    
    //�ȴ��������ӵ����ʱ�䣬��λ���룬Ĭ��ֵΪ-1����ʾ������ʱ����������ȴ�ʱ�䣬��ֱ���׳�JedisConnectionException��
    private static int MAX_WAIT = 10000;
    
    private static int TIMEOUT = 10000;
    
    //��borrowһ��jedisʵ��ʱ���Ƿ���ǰ����validate���������Ϊtrue����õ���jedisʵ�����ǿ��õģ�
    private static boolean TEST_ON_BORROW = true;
    
    private static JedisPool jedisPool = null;
    
    private static ThreadLocal<Jedis> threadLocal = new ThreadLocal<Jedis>();
    
    /**
     * ��ʼ��Redis���ӳ�
     */
    static {
        try {
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxActive(MAX_ACTIVE);
            config.setMaxIdle(MAX_IDLE);
            config.setMaxWait(MAX_WAIT);
            config.setTestOnBorrow(TEST_ON_BORROW);
            jedisPool = new JedisPool(config, ADDR, PORT, TIMEOUT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * ��ȡJedisʵ��
     * @return
     */
    public static Jedis getJedis() {
    	Jedis resource = null;
        try {
        	resource = threadLocal.get();
        	if(resource != null){
        		return resource;
        	}
        	resource = jedisPool.getResource();
        	threadLocal.set(resource);
        	return resource;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resource;
    }
    
    /**
     * �ͷ�jedis��Դ
     * @param jedis
     */
    public static void returnResource() {
    	Jedis jedis = threadLocal.get();
        if (jedis != null) {
        	threadLocal.remove();
            jedisPool.returnResource(jedis);
        }
    }
}