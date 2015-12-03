package org.lionsoul.jcseg.server.controller;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.lionsoul.jcseg.extractor.impl.TextRankSummaryExtractor;
import org.lionsoul.jcseg.server.JcsegController;
import org.lionsoul.jcseg.server.GlobalProjectSetting;
import org.lionsoul.jcseg.server.GlobalResourcePool;
import org.lionsoul.jcseg.server.core.UriEntry;
import org.lionsoul.jcseg.tokenizer.SentenceSeg;
import org.lionsoul.jcseg.tokenizer.core.ADictionary;
import org.lionsoul.jcseg.tokenizer.core.ISegment;
import org.lionsoul.jcseg.tokenizer.core.JcsegException;
import org.lionsoul.jcseg.tokenizer.core.JcsegTaskConfig;
import org.lionsoul.jcseg.tokenizer.core.SegmentFactory;

/**
 * keywords extractor handler
 * 
 * @author chenxin<chenxin619315@gmail.com>
*/
public class SentenceController extends JcsegController
{

	public SentenceController(
			GlobalProjectSetting setting,
			GlobalResourcePool resourcePool, 
			UriEntry uriEntry,
			Request baseRequest, 
			HttpServletRequest request,
			HttpServletResponse response) throws IOException
	{
		super(setting, resourcePool, uriEntry, baseRequest, request, response);
	}

	@Override
	protected void run(String method) throws IOException
	{
		String text = getString("text");
		int number = getInt("number", 6);
		if ( text == null || "".equals(text) )
		{
			response(false, 1, "Invalid Arguments");
			return;
		}
		
		JcsegTaskConfig config = resourcePool.getConfig("extractor");
		ADictionary dic = resourcePool.getDic("main");
		
		try {
			ISegment seg = SegmentFactory
					.createJcseg(JcsegTaskConfig.COMPLEX_MODE, new Object[]{config, dic});
			long s_time = System.nanoTime();
			TextRankSummaryExtractor extractor = new TextRankSummaryExtractor(seg, new SentenceSeg());
			extractor.setSentenceNum(number);
			
			Map<String, Object> map = new HashMap<String, Object>();
			DecimalFormat df = new DecimalFormat("0.00000"); 
			map.put("took", df.format((System.nanoTime() - s_time)/1E9));
			map.put("sentence", extractor.getKeySentenceFromString(text));
			
			//response the request
			response(true, 0, map);
		} catch (JcsegException e) {
			response(false, -1, "Internal error...");
		}
	}

}