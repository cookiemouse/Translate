package com.cookiemouse.simple_ts;

public class Language {
	
	private int imageId;
	private String name;

	public Language(int imageId, String name)
	{
		this.imageId = imageId;
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}
	
	public int getImageId()
	{
		return imageId;
	}
}
