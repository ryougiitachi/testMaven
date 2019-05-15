package per.itachi.test.redis.entity;

import com.alibaba.fastjson.annotation.JSONField;

public class RedisResponseEntity {
	
	@JSONField(name="result-code")
	private int resultCode;

	public int getResultCode() {
		return resultCode;
	}

	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}
}
