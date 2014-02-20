/**
 * -----------------------------------------------------------------------
 * Copyright 2013 - Alistair Rutherford - www.netthreads.co.uk
 * -----------------------------------------------------------------------
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package com.netthreads.transportation.parser.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;

import com.netthreads.transportation.parser.PullParser;

/**
 * This is an alternative implementation of the pull parser. Not as fast but the
 * code is slightly more elegant.
 * 
 * Type: Traffic Data.
 * 
 * Sample URL:
 * http://dashboard.glasgow.gov.uk/api/live/trafficEvents.php?type=xml
 * 
 */
public class TrafficDataPullParserEx implements PullParser<TrafficData>
{
	private XmlPullParser parser = null;
	
	// In tag
	
	// Remove all whitespace over 2 characters in length.
	private static final String REGEX_DESCRIPTION = "\\s{2,}";
	
	// Record values.
	private String id;
	private String description;
	private String temp;
	private String tempType;
	private String localLinkName;
	private String linkName;
	private String townName;
	private String type;
	private String latitude;
	private String longitude;
	private String overallStartTime;
	private String overallEndTime;
	
	/**
	 * Tag Map.
	 */
	@SuppressWarnings("serial")
	private Map<String, Boolean> inTagMap = new HashMap<String, Boolean>()
	{
		{
			put(TrafficData.TAG_SITUATION_RECORD, false);
			put(TrafficData.TAG_LATITUDE, false);
			put(TrafficData.TAG_LONGITUDE, false);
			put(TrafficData.TAG_NON_GENERAL_PUBLIC_COMMENT, false);
			put(TrafficData.TAG_COMMENT, false);
			put(TrafficData.TAG_VALUE, false);
			put(TrafficData.TAG_NETWORK_MANAGEMENT_TYPE, false);
			put(TrafficData.TAG_POOR_ROAD_INFRASTRUCTURETYPE, false);
			put(TrafficData.TAG_PUBLIC_EVENT_TYPE, false);
			put(TrafficData.TAG_ROAD_MAINTENANCE_TYPE, false);
			put(TrafficData.TAG_POINT, false);
			put(TrafficData.TAG_NAME, false);
			put(TrafficData.TAG_DESCRIPTOR, false);
			put(TrafficData.TAG_TPEG_DESCRIPTOR_TYPE, false);
			put(TrafficData.TAG_LOCAL_LINK_NAME, false);
			put(TrafficData.TAG_LINK_NAME, false);
			put(TrafficData.TAG_OVERALL_END_TIME, false);
			put(TrafficData.TAG_OVERALL_START_TIME, false);
		}
	};
	
	/**
	 * Construct parser.
	 * 
	 */
	public TrafficDataPullParserEx(XmlPullParser parser)
	{
		this.parser = parser;
		
		reset();
	}
	
	/**
	 * Process start tag
	 * 
	 * @param tag
	 */
	@Override
	public boolean processStartTag(String tag)
	{
		if (tag.equals(TrafficData.TAG_SITUATION_RECORD))
		{
			String namespace = parser.getNamespace();
			id = parser.getAttributeValue(namespace, TrafficData.TEXT_ID);
		}
		
		if (inTagMap.containsKey(tag))
		{
			inTagMap.put(tag, true);
		}
		
		return false;
	}
	
	/**
	 * Process end tag
	 * 
	 * @param tag
	 */
	@Override
	public boolean processEndTag(String tag)
	{
		boolean ready = false;
		
		Boolean inSituationRecord = inTagMap.get(TrafficData.TAG_SITUATION_RECORD);
		
		if (inSituationRecord != null)
		{
			if (tag.equals(TrafficData.TAG_SITUATION_RECORD))
			{
				ready = true;
			}
			else if (inSituationRecord && tag.equals(TrafficData.TAG_TPEG_DESCRIPTOR_TYPE))
			{
				// Handle end condition when we have picked up a descriptor
				// value.
				if (!tempType.isEmpty())
				{
					if (tempType.equals(TrafficData.TAG_LOCAL_LINK_NAME))
					{
						localLinkName = temp;
					}
					else if (tempType.equals(TrafficData.TAG_LINK_NAME))
					{
						linkName = temp;
					}
					else if (tempType.equals(TrafficData.TAG_TOWN_NAME))
					{
						townName = temp;
					}
				}
			}
			
			if (inTagMap.containsKey(tag))
			{
				inTagMap.put(tag, false);
			}
		}
		
		return ready;
	}
	
	/**
	 * Collect text values depending on conditions.
	 * 
	 * @param text
	 */
	@Override
	public void processText(String text)
	{
		// Old way was easier to understand.
		boolean inSituationRecord = inTagMap.get(TrafficData.TAG_SITUATION_RECORD);
		boolean inPoint = inTagMap.get(TrafficData.TAG_POINT);
		boolean inLongitude = inTagMap.get(TrafficData.TAG_LONGITUDE);
		
		if (inSituationRecord)
		{
			if (inPoint && inLongitude)
			{
				longitude = text;
			}
			else
			{
				boolean inLatitude = inTagMap.get(TrafficData.TAG_LATITUDE);
				
				if (inPoint && inLatitude)
				{
					latitude = text;
				}
				else
				{
					boolean inDescriptor = inTagMap.get(TrafficData.TAG_DESCRIPTOR);
					boolean inValue = inTagMap.get(TrafficData.TAG_VALUE);
					
					if (inPoint & inDescriptor && inValue)
					{
						temp = text;
					}
					else
					{
						boolean inTpegDescriptorType = inTagMap.get(TrafficData.TAG_TPEG_DESCRIPTOR_TYPE);
						
						if (inPoint && inTpegDescriptorType)
						{
							tempType = text;
						}
						else
						{
							boolean inEventType = inTagMap.get(TrafficData.TAG_NETWORK_MANAGEMENT_TYPE) || inTagMap.get(TrafficData.TAG_POOR_ROAD_INFRASTRUCTURETYPE) || inTagMap.get(TrafficData.TAG_PUBLIC_EVENT_TYPE) || inTagMap.get(TrafficData.TAG_ROAD_MAINTENANCE_TYPE);
							
							if (inEventType)
							{
								type = text;
							}
							else
							{
								boolean inOverallStartTime = inTagMap.get(TrafficData.TAG_OVERALL_START_TIME);
								
								if (inOverallStartTime)
								{
									overallStartTime = text;
								}
								else
								{
									boolean inOverallEndTime = inTagMap.get(TrafficData.TAG_OVERALL_END_TIME);
									
									if (inOverallEndTime)
									{
										overallEndTime = text;
									}
									else
									{
										boolean inNonGeneralPublicComment = inTagMap.get(TrafficData.TAG_NON_GENERAL_PUBLIC_COMMENT);
										boolean inComment = inTagMap.get(TrafficData.TAG_COMMENT);
										
										if (inNonGeneralPublicComment && inComment && inValue)
										{
											description = text.replaceAll(REGEX_DESCRIPTION, " ");
										}
									}
									
								}
							}
							
						}
					}
				}
			}
		}
	}
	
	/**
	 * Build record from parsed data.
	 * 
	 */
	@Override
	public void populateRecord(TrafficData record)
	{
		// Populate record.
		record.setId(id);
		record.setDescription(description);
		record.setLinkName(linkName);
		record.setLocalLinkName(localLinkName);
		record.setTownName(townName);
		record.setType(type);
		record.setLatitude(latitude);
		record.setLongitude(longitude);
		record.setOverallStartTime(overallStartTime);
		record.setOverallEndTime(overallEndTime);
		
		// Reset parser fields.
		reset();
	}
	
	/**
	 * Reset parsed strings.
	 * 
	 */
	@Override
	public void reset()
	{
		id = "";
		description = "";
		localLinkName = "";
		linkName = "";
		townName = "";
		type = TrafficData.TEXT_UNKNOWN;
		latitude = "";
		longitude = "";
		overallStartTime = "";
		overallEndTime = "";
	}
	
	/**
	 * Inside tag
	 * 
	 * @return True if inside tag.
	 */
	@Override
	public boolean inTarget()
	{
		boolean state = false;
		Iterator<Boolean> iterator = inTagMap.values().iterator();
		while (!state && iterator.hasNext())
		{
			Boolean item = iterator.next();
			state |= item;
		}
		
		return state;
	}
	
}
