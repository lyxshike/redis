package com.bjsxt.test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.junit.BeforeClass;
import org.junit.Test;

import redis.clients.jedis.Jedis;

import com.alibaba.fastjson.JSON;
import com.bjsxt.entity.User;

public class TestRedis {
	
	public static  Jedis j;
	
	@BeforeClass
	public static void init(){
		j = new Jedis("192.168.0.120", 6379);
	}
	
	@Test
	public void test(){
		System.out.println(j.get("name")+"  "+j.get("age"));	
	}
	
	@Test
	public void testMap(){
		Map<String, String> map = new HashMap<String, String>();
		map.put("name", "xingxing");
		map.put("age", "22");
		map.put("qq", "123456");
		j.hmset("user",map);
	}
	
	@Test
	public void testUser(){
		//User 对象数据量很大， 查询很频繁， 需要把user表里的数据都放入到缓存中去
		// 做放入操作
		// UUID
		
		Map<String,String> map = new HashMap<String, String>();
		
		String u1id = UUID.randomUUID().toString();
		User u1 = new User(u1id,"z3", 23, "m");
		map.put(u1id, JSON.toJSONString(u1));
		
		String u2id = UUID.randomUUID().toString();
		User u2 = new User(u2id,"z3", 25, "w");
		map.put(u2id, JSON.toJSONString(u2));
		
		String u3id = UUID.randomUUID().toString();
		User u3 = new User(u3id,"z3", 25, "w");
		map.put(u3id, JSON.toJSONString(u3));
		
		String u4id = UUID.randomUUID().toString();
		User u4 = new User(u4id,"z3", 29, "m");
		map.put(u4id, JSON.toJSONString(u4));
		
		String u5id = UUID.randomUUID().toString();
		User u5 = new User(u5id,"z3", 28, "m");
		map.put(u5id, JSON.toJSONString(u5));
		
		j.hmset("SYS_USER_TABLE", map);
		

		
		
	}
	
	@Test
	public void TestMultip(){
		
		//1 .实现    select * from user where age = 25
		// 2. 实现   select * from user where age =25 and sex = "m"
		
		//想想，多种集合配合使用， hash 和 set类型 同时使用了
		
		/*
		 *    指定业务 查询业务  SYS_USER_SEL_AGE_25
		 *    
		 *    指定业务 查询业务  SYS_USER_SEX_m
		 *    
		 *    指定业务  查询业务  SYS_USER_SEL_SEX_w
		 * 
		 * */
		
		final String SYS_USER_SEL_AGE_25 ="SYS_USER_SEL_AGE_25";
		final String  SYS_USER_SEL_SEX_m = " SYS_USER_SEL_SEX_m";
		final String  SYS_USER_SEL_SEX_w = "SYS_USER_SEL_SEX_w";
		final String SYS_USER_TABLE = "SYS_USER_TABLE";
		
	/*	
		
        Map<String,String> map = new HashMap<String, String>();
		
		String u1id = UUID.randomUUID().toString();
		User u1 = new User(u1id,"z3", 23, "m");
		map.put(u1id, JSON.toJSONString(u1));
		j.sadd(SYS_USER_SEL_SEX_m, u1id);
		
		String u2id = UUID.randomUUID().toString();
		User u2 = new User(u2id,"z3", 25, "w");
		map.put(u2id, JSON.toJSONString(u2));
		j.sadd(SYS_USER_SEL_AGE_25, u2id);
		j.sadd(SYS_USER_SEL_SEX_w, u2id);
		
		String u3id = UUID.randomUUID().toString();
		User u3 = new User(u3id,"z3", 25, "w");
		map.put(u3id, JSON.toJSONString(u3));
		j.sadd(SYS_USER_SEL_AGE_25, u3id);
		j.sadd(SYS_USER_SEL_SEX_w, u3id);
		
		String u4id = UUID.randomUUID().toString();
		User u4 = new User(u4id,"z3", 29, "m");
		map.put(u4id, JSON.toJSONString(u4));
		j.sadd(SYS_USER_SEL_SEX_m, u4id);
		
		String u5id = UUID.randomUUID().toString();
		User u5 = new User(u5id,"z3", 28, "m");
		map.put(u5id, JSON.toJSONString(u5));
		j.sadd(SYS_USER_SEL_SEX_m, u5id);
		
		j.hmset("SYS_USER_TABLE", map);
		
		*/
		
		Set<String> user_ages =j.smembers(SYS_USER_SEL_AGE_25);
		for (Iterator iterator = user_ages.iterator(); iterator.hasNext();) {
			String string = (String) iterator.next();
			String ret = j.hget(SYS_USER_TABLE, string);
			System.out.println(ret);
		}
		
		Set<String> user_age25_sexm = j.sinter(SYS_USER_SEL_AGE_25, SYS_USER_SEL_SEX_w);
		for (Iterator iterator = user_age25_sexm.iterator(); iterator.hasNext();) {
			String string = (String) iterator.next();
			 String usr = j.hget(SYS_USER_TABLE, string);
		     User user = JSON.parseObject(usr, User.class);
		     System.out.println(user.toString());
		}
		
	}
}
