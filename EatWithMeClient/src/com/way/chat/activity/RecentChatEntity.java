package com.way.chat.activity;

public class RecentChatEntity {
	private int id;
	private int img;
	private int count;
	private String name;
	private String time;
	private String msg;

	public RecentChatEntity() {
	}

	public RecentChatEntity(int id, int img, int count, String name,
			String time, String msg) {
		super();
		this.id = id;
		this.img = img;
		this.count = count;
		this.name = name;
		this.time = time;
		this.msg = msg;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getImg() {
		return img;
	}

	public void setImg(int img) {
		this.img = img;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public boolean equals(Object object) {
		if (object == null)
			return false;

		if (object == this)
			return true;

		if (object instanceof RecentChatEntity) {
			RecentChatEntity entity = (RecentChatEntity) object;
			if (entity.id == this.id)
				return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return "RecentChatEntity [id=" + id + ", img=" + img + ", count="
				+ count + ", name=" + name + ", time=" + time + ", msg=" + msg
				+ "]";
	}
}
