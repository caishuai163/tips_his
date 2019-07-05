package cn.gyyx.first.beans;

public class UserInfo {
	private int id;
	private int uid;
	private String name;
	private String pid;
	private String address;
	private long tel;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public long getTel() {
		return tel;
	}

	public void setTel(long tel) {
		this.tel = tel;
	}

	@Override
	public String toString() {
		return "UserInfo [id=" + id + ", uid=" + uid + ", name=" + name + ", pid=" + pid + ", address=" + address
				+ ", tel=" + tel + "]";
	}

	public UserInfo(int id, int uid, String name, String pid, String address, long tel) {
		super();
		this.id = id;
		this.uid = uid;
		this.name = name;
		this.pid = pid;
		this.address = address;
		this.tel = tel;
	}

	public UserInfo() {
		super();
		// TODO Auto-generated constructor stub
	}

}
