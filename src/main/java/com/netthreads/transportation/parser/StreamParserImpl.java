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
package com.netthreads.transportation.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

/**
 * Stream parser implementation.
 * 
 */
public class StreamParserImpl<T> implements StreamParser<T>
{
	public static final String TEXT_LINK_DELIMETER = ";";
	public static final String TEXT_TITLE_DELIMETER = " ";
	
	private XmlPullParserFactory factory = null;
	private XmlPullParser parser = null;
	
	// Control
	private int state = WAITING;
	
	public StreamParserImpl() throws XmlPullParserException
	{
		factory = XmlPullParserFactory.newInstance();
		
		factory.setNamespaceAware(false);
		
		parser = factory.newPullParser();
	}
	
	/**
	 * Fetch and parse data.
	 * 
	 * @param stream
	 * @param list
	 * @param dataFactory
	 */
	@Override
	public int fetch(InputStream stream, List<T> list, DataFactory<T> dataFactory, PullParser<T> pullParser)
	{
		state = BUSY;
		
		reset();
		
		try
		{
			// Assign stream of input.
			parser.setInput(stream, null);
			
			int type; // received event type
			
			while (((type = parser.nextToken()) != XmlPullParser.END_DOCUMENT) && (state != CANCELLED))
			{
				if (type == XmlPullParser.TEXT)
				{
					String text = parser.getText();
					
					if (text != null)
					{
						pullParser.processText(text.trim());
					}
				}
				else if (type == XmlPullParser.START_TAG)
				{
					String startTag = parser.getName();
					
					if (startTag != null)
					{
						pullParser.processStartTag(startTag);
					}
				}
				else if (type == XmlPullParser.END_TAG)
				{
					String endTag = parser.getName();
					
					if (endTag != null)
					{
						if (pullParser.processEndTag(endTag))
						{
							// Create a new holding record
							T data = dataFactory.createRecord();
							
							pullParser.populateRecord(data);
							
							list.add(data);
						}
					}
				}
			}
		}
		catch (XmlPullParserException e)
		{
			// Oops
			state = ERROR;
		}
		catch (IOException e)
		{
			// Oops
			state = ERROR;
		}
		
		if (state != CANCELLED && state != ERROR)
		{
			state = DONE;
		}
		
		return state;
	}
	
	/**
	 * Stops the handler process.
	 * 
	 */
	@Override
	public void cancel()
	{
		state = CANCELLED;
	}
	
	/**
	 * Reset parser state.
	 * 
	 */
	@Override
	public void reset()
	{
		state = WAITING;
	}
	
	/**
	 * Have to implement this unfortunately.
	 * 
	 */
	@Override
	public XmlPullParser getParser()
	{
		return parser;
	}
	
	/**
	 * Return parser state code.
	 * 
	 */
	@Override
	public int getState()
	{
		return state;
	}
	
}
