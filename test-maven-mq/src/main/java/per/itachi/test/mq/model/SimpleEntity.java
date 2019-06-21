package per.itachi.test.mq.model;

import java.io.Serializable;

import com.alibaba.fastjson.JSON;

public class SimpleEntity implements Serializable{
	
	private int id;
	
	private String name;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "SimpleEntity " + JSON.toJSONString(this);
	}
}