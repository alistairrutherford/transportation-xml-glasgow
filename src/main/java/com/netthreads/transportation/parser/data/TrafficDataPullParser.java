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

import org.xmlpull.v1.XmlPullParser;

import com.netthreads.transportation.parser.PullParser;

/**
 * This is a _simple_ XML Pull parser for Glasgow Traffic Data.
 * 
 * Type: Traffic Data.
 * 
 * Sample URL:
 * http://dashboard.glasgow.gov.uk/api/live/trafficEvents.php?type=xml
 * 
 */
public class TrafficDataPullParser implements PullParser<TrafficData>
{
	private XmlPullParser parser = null;
	
	// In tag
	private boolean inSituationRecord = false;
	private boolean inLatitude = false;
	private boolean inLongitude = false;
	private boolean inNonGeneralPublicComment = false;
	private boolean inComment = false;
	private boolean inPoint = false;
	private boolean inName = false;
	private boolean inDescriptor = false;
	private boolean inValue = false;
	private boolean inEventType = false;
	private boolean inTpegDescriptorType = false;
	private boolean inOverallStartTime = false;
	private boolean inOverallEndTime = false;
	
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
	 * Construct parser.
	 * 
	 */
	public TrafficDataPullParser(XmlPullParser parser)
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
			inSituationRecord = true;
			
			String namespace = parser.getNamespace();
			id = parser.getAttributeValue(namespace, TrafficData.TEXT_ID);
		}
		else if (inSituationRecord && tag.equals(TrafficData.TAG_LATITUDE))
		{
			inLatitude = true;
		}
		else if (inSituationRecord && tag.equals(TrafficData.TAG_LONGITUDE))
		{
			inLongitude = true;
		}
		else if (inSituationRecord && tag.equals(TrafficData.TAG_NON_GENERAL_PUBLIC_COMMENT))
		{
			inNonGeneralPublicComment = true;
		}
		else if (inSituationRecord && tag.equals(TrafficData.TAG_COMMENT))
		{
			inComment = true;
		}
		else if (inSituationRecord && tag.equals(TrafficData.TAG_POINT))
		{
			inPoint = true;
		}
		else if (inSituationRecord && tag.equals(TrafficData.TAG_NAME))
		{
			inName = true;
		}
		else if (inSituationRecord && tag.equals(TrafficData.TAG_DESCRIPTOR))
		{
			inDescriptor = true;
		}
		else if (inSituationRecord && tag.equals(TrafficData.TAG_VALUE))
		{
			inValue = true;
		}
		else if (inSituationRecord && tag.equals(TrafficData.TAG_TPEG_DESCRIPTOR_TYPE))
		{
			inTpegDescriptorType = true;
		}
		else if (inSituationRecord && tag.equals(TrafficData.TAG_OVERALL_START_TIME))
		{
			inOverallStartTime = true;
		}
		else if (inSituationRecord && tag.equals(TrafficData.TAG_OVERALL_END_TIME))
		{
			inOverallEndTime = true;
		}
		else if (inSituationRecord && tag.equals(TrafficData.TAG_NETWORK_MANAGEMENT_TYPE))
		{
			inEventType = true;
		}
		else if (inSituationRecord && tag.equals(TrafficData.TAG_POOR_ROAD_INFRASTRUCTURETYPE))
		{
			inEventType = true;
		}
		else if (inSituationRecord && tag.equals(TrafficData.TAG_PUBLIC_EVENT_TYPE))
		{
			inEventType = true;
		}
		else if (inSituationRecord && tag.equals(TrafficData.TAG_ROAD_MAINTENANCE_TYPE))
		{
			inEventType = true;
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
		if (tag.equals(TrafficData.TAG_SITUATION_RECORD))
		{
			inSituationRecord = false;
			
			ready = true;
		}
		else if (inSituationRecord && tag.equals(TrafficData.TAG_LATITUDE))
		{
			inLatitude = false;
		}
		else if (inSituationRecord && tag.equals(TrafficData.TAG_LONGITUDE))
		{
			inLongitude = false;
		}
		else if (inSituationRecord && tag.equals(TrafficData.TAG_NON_GENERAL_PUBLIC_COMMENT))
		{
			inNonGeneralPublicComment = false;
		}
		else if (inSituationRecord && tag.equals(TrafficData.TAG_COMMENT))
		{
			inComment = false;
		}
		else if (inSituationRecord && tag.equals(TrafficData.TAG_POINT))
		{
			inPoint = false;
		}
		else if (inSituationRecord && tag.equals(TrafficData.TAG_NAME))
		{
			inName = false;
		}
		else if (inSituationRecord && tag.equals(TrafficData.TAG_DESCRIPTOR))
		{
			inDescriptor = false;
		}
		else if (inSituationRecord && tag.equals(TrafficData.TAG_VALUE))
		{
			inValue = false;
		}
		else if (inSituationRecord && tag.equals(TrafficData.TAG_TPEG_DESCRIPTOR_TYPE))
		{
			inTpegDescriptorType = false;
			
			// Handle end condition when we have picked up a descriptor value.
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
		else if (inSituationRecord && tag.equals(TrafficData.TAG_OVERALL_START_TIME))
		{
			inOverallStartTime = false;
		}
		else if (inSituationRecord && tag.equals(TrafficData.TAG_OVERALL_END_TIME))
		{
			inOverallEndTime = false;
		}
		else if (inSituationRecord && tag.equals(TrafficData.TAG_NETWORK_MANAGEMENT_TYPE))
		{
			inEventType = false;
		}
		else if (inSituationRecord && tag.equals(TrafficData.TAG_POOR_ROAD_INFRASTRUCTURETYPE))
		{
			inEventType = false;
		}
		else if (inSituationRecord && tag.equals(TrafficData.TAG_PUBLIC_EVENT_TYPE))
		{
			inEventType = false;
		}
		else if (inSituationRecord && tag.equals(TrafficData.TAG_ROAD_MAINTENANCE_TYPE))
		{
			inEventType = false;
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
		if (inSituationRecord && inPoint && inLongitude)
		{
			longitude = text;
		}
		else if (inSituationRecord && inPoint && inLatitude)
		{
			latitude = text;
		}
		else if (inSituationRecord && inNonGeneralPublicComment && inComment && inValue)
		{
			description = text.replaceAll(REGEX_DESCRIPTION, " ");
		}
		else if (inSituationRecord && inPoint && inName && inDescriptor && inValue)
		{
			temp = text;
		}
		else if (inTpegDescriptorType)
		{
			tempType = text;
		}
		else if (inEventType)
		{
			type = text;
		}
		else if (inOverallStartTime)
		{
			overallStartTime = text;
		}
		else if (inOverallEndTime)
		{
			overallEndTime = text;
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
		return inSituationRecord || inLatitude || inLongitude || inNonGeneralPublicComment || inComment || inPoint || inName || inDescriptor || inValue || inEventType || inOverallStartTime || inOverallEndTime;
		
	}
	
}
