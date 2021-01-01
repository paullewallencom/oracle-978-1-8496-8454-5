package com.oracle.cep.event;

public class ShipPosEvent {

	private int shipId ;
	private int seq ;
	private double latitude ;
	private double longitude ;
	private String info ;
	
	public int getShipId() {
		return shipId;
	}
	public void setShipId(int shipId) {
		this.shipId = shipId;
	}
	public int getSeq() {
		return seq;
	}
	public void setSeq(int seq) {
		this.seq = seq;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	@Override
	public String toString() {
		return "ShipPosEvent [shipId=" + shipId + ", seq=" + seq
				+ ", latitude=" + latitude + ", longitude=" + longitude
				+ ", info=" + info + "]";
	}
	
	
	
}
