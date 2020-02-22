package com.rudrik.noteapp.models.distance;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DistanceData{

	@SerializedName("destination_addresses")
	private List<String> destinationAddresses;

	@SerializedName("rows")
	private List<RowsItem> rows;

	@SerializedName("origin_addresses")
	private List<String> originAddresses;

	@SerializedName("status")
	private String status;

	public void setDestinationAddresses(List<String> destinationAddresses){
		this.destinationAddresses = destinationAddresses;
	}

	public List<String> getDestinationAddresses(){
		return destinationAddresses;
	}

	public void setRows(List<RowsItem> rows){
		this.rows = rows;
	}

	public List<RowsItem> getRows(){
		return rows;
	}

	public void setOriginAddresses(List<String> originAddresses){
		this.originAddresses = originAddresses;
	}

	public List<String> getOriginAddresses(){
		return originAddresses;
	}

	public void setStatus(String status){
		this.status = status;
	}

	public String getStatus(){
		return status;
	}

	@Override
 	public String toString(){
		return 
			"DistanceData{" + 
			"destination_addresses = '" + destinationAddresses + '\'' + 
			",rows = '" + rows + '\'' + 
			",origin_addresses = '" + originAddresses + '\'' + 
			",status = '" + status + '\'' + 
			"}";
		}
}