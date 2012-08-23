/*
 * Copyright 2012 Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.facebook.samples.socialcafe;


public class Drink {
	private int imageID;
	private String title;
	private String info;
	private String url;
	private String size;
	
	public Drink(String title, String info, int imageID, String size, String url) {
		this.title = title;
		this.info = info;
		this.imageID = imageID;
		this.size = size;
		this.url = url;
	}
	
	public int getImageID() {
		return imageID;
	}

	public String getTitle() {
		return title;
	}

	public String getInfo() {
		return info;
	}

	
	public String getURL() {
		return this.url;
	}
	
	public String getSize() {
		return size;
	}
}
