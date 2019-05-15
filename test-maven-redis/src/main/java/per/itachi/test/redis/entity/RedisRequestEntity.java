package per.itachi.test.redis.entity;

import com.alibaba.fastjson.annotation.JSONField;

public class RedisRequestEntity {
	
	/**
	 * 1-string 2-list 3-hash 4-set 5-zset 
	 * */
	@JSONField(name="date-type")
	private int dataType;
	
	/**
	 * 1-add 2-delete 
	 * */
	@JSONField(name="ops")
	private int ops;
	
	@JSONField(name="key")
	private String key;
	
	@JSONField(name="value")
	private String value;

	public int getDataType() {
		return dataType;
	}

	public void setDataType(int dataType) {
		this.dataType = dataType;
	}

	public int getOps() {
		return ops;
	}

	public void setOps(int ops) {
		this.ops = ops;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
