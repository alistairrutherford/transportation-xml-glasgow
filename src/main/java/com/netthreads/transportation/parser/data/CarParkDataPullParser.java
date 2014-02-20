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
 * This is a _simple_ XML Pull parser for Glasgow Car Park occupancy.
 * 
 * Type: Car Park Data
 * 
 * Sample URL: http://dashboard.glasgow.gov.uk/api/live/parking.php?type=xml
 * 
 */
public class CarParkDataPullParser implements PullParser<CarParkData>
{
	private XmlPullParser parser = null;

	// In tag
	private boolean inSituationRecord = false;
	private boolean inLatitude = false;
	private boolean inLongitude = false;
	private boolean inCarParkIdentity = false;
	private boolean inCarParkOccupancy = false;
	private boolean inCarParkStatus = false;
	private boolean inOccupiedSpaces = false;
	private boolean inTotalCapacity = false;

	// Record values.
	private String id;
	private String latitude;
	private String longitude;
	private String carParkIdentity;
	private String carParkOccupancy;
	private String carParkStatus;
	private String occupiedSpaces;
	private String totalCapacity;

	/**
	 * Construct parser.
	 * 
	 */
	public CarParkDataPullParser(XmlPullParser parser)
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
		if (tag.equals(CarParkData.TEXT_SITUATION_RECORD))
		{
			inSituationRecord = true;

			String namespace = parser.getNamespace();
			id = parser.getAttributeValue(namespace, CarParkData.TEXT_ID);
		}
		else if (inSituationRecord && tag.equals(CarParkData.TEXT_LATITUDE))
		{
			inLatitude = true;
		}
		else if (inSituationRecord && tag.equals(CarParkData.TEXT_LONGITUDE))
		{
			inLongitude = true;
		}
		else if (inSituationRecord && tag.equals(CarParkData.TEXT_CAR_PARK_IDENTITY))
		{
			inCarParkIdentity = true;
		}
		else if (inSituationRecord && tag.equals(CarParkData.TEXT_CAR_PARK_OCCUPANCY))
		{
			inCarParkOccupancy = true;
		}
		else if (inSituationRecord && tag.equals(CarParkData.TEXT_CAR_PARK_STATUS))
		{
			inCarParkStatus = true;
		}
		else if (inSituationRecord && tag.equals(CarParkData.TEXT_OCCUPIED_SPACES))
		{
			inOccupiedSpaces = true;
		}
		else if (inSituationRecord && tag.equals(CarParkData.TEXT_TOTAL_CAPACITY))
		{
			inTotalCapacity = true;
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
		if (tag.equals(CarParkData.TEXT_SITUATION_RECORD))
		{
			inSituationRecord = false;

			ready = true;
		}
		else if (inSituationRecord && tag.equals(CarParkData.TEXT_LATITUDE))
		{
			inLatitude = false;
		}
		else if (inSituationRecord && tag.equals(CarParkData.TEXT_LONGITUDE))
		{
			inLongitude = false;
		}
		else if (inSituationRecord && tag.equals(CarParkData.TEXT_CAR_PARK_IDENTITY))
		{
			inCarParkIdentity = false;
		}
		else if (inSituationRecord && tag.equals(CarParkData.TEXT_CAR_PARK_OCCUPANCY))
		{
			inCarParkOccupancy = false;
		}
		else if (inSituationRecord && tag.equals(CarParkData.TEXT_CAR_PARK_STATUS))
		{
			inCarParkStatus = false;
		}
		else if (inSituationRecord && tag.equals(CarParkData.TEXT_OCCUPIED_SPACES))
		{
			inOccupiedSpaces = false;
		}
		else if (inSituationRecord && tag.equals(CarParkData.TEXT_TOTAL_CAPACITY))
		{
			inTotalCapacity = false;
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
		if (inSituationRecord && inLongitude)
		{
			longitude = text;
		}
		else if (inSituationRecord && inLatitude)
		{
			latitude = text;
		}
		else if (inSituationRecord && inCarParkIdentity)
		{
			carParkIdentity = text;
		}
		else if (inSituationRecord && inCarParkOccupancy)
		{
			carParkOccupancy = text;
		}
		else if (inSituationRecord && inCarParkStatus)
		{
			carParkStatus = text;
		}
		else if (inSituationRecord && inOccupiedSpaces)
		{
			occupiedSpaces = text;
		}
		else if (inSituationRecord && inTotalCapacity)
		{
			totalCapacity = text;
		}
	}

	/**
	 * Build record from parsed data.
	 * 
	 */
	@Override
	public void populateRecord(CarParkData record)
	{
		// Populate record.
		record.setId(id);
		record.setLatitude(latitude);
		record.setLongitude(longitude);
		record.setCarParkIdentity(carParkIdentity);
		record.setCarParkOccupancy(carParkOccupancy);
		record.setCarParkStatus(carParkStatus);
		record.setOccupiedSpaces(occupiedSpaces);
		record.setTotalCapacity(totalCapacity);

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
		latitude = "";
		longitude = "";
		carParkIdentity = "";
		carParkOccupancy = "";
		carParkStatus = "";
		occupiedSpaces = "";
		totalCapacity = "";
	}

	/**
	 * Inside tag
	 * 
	 * @return True if inside tag.
	 */
	@Override
	public boolean inTarget()
	{
		return inSituationRecord || inLatitude || inLongitude || inCarParkIdentity || inCarParkOccupancy || inCarParkStatus || inOccupiedSpaces || inTotalCapacity;
	}

}
