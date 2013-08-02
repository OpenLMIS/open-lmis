package org.openlmis.web.controller;

import com.sun.syndication.feed.atom.Content;
import com.sun.syndication.feed.atom.Entry;
import com.sun.syndication.feed.atom.Feed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.WireFeedOutput;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.ict4h.atomfeed.server.service.EventFeedService;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.openlmis.core.exception.DataException;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class VendorEventFeedServiceHelper{

  public static String getRecentFeed(EventFeedService eventFeedService, String requestURL, Logger logger, String vendor, String category){
    try {
      Feed feed = eventFeedService.getRecentFeed(new URI(requestURL), category);
      mapFeedBasedOnVendorAndCategory(feed, vendor, category);
      return new WireFeedOutput().outputString(feed);
    } catch (URISyntaxException e) {
      throw new RuntimeException("Bad URI", e);
    } catch (Exception e) {
      logger.error("error occurred while getting recent feedgenerator", e);
      throw new RuntimeException("Unexpected error", e); //TODO
    }
  }

  private static void mapFeedBasedOnVendorAndCategory(Feed feed, String vendor, String category) throws IOException {
    if (vendor == null) {
      return;
    }

    String template = "vendorMapping_"+ vendor + "_" + category +".xml";

    Map<String, String> map = createTemplateMap(template);

    Pattern pattern = Pattern.compile("(?:<!\\[CDATA\\[)(.)*(?:\\]\\]>)");
    List<Entry> feedEntries = feed.getEntries();
    for (Entry entry : feedEntries) {
      List<Content> contentList = entry.getContents();
      for( Content content : contentList){
        String value = pattern.matcher(content.getValue()).group();
        JsonNode rootNode = convertToTemplate(map, value);
        content.setValue("<![CDATA["+rootNode.toString()+"]]>");
      }
    }

  }

  private static JsonNode convertToTemplate(Map<String, String> map, String value) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode rootNode = objectMapper.readTree(value);
    Iterator<Map.Entry<String,JsonNode>> iterator =  rootNode.getFields();
    ObjectNode returnedNode = new ObjectNode(JsonNodeFactory.instance);
    while(iterator.hasNext()){
      Map.Entry<String, JsonNode> jsonNode = iterator.next();
      String fieldName = jsonNode.getKey();
      String mappedName = map.get(fieldName);
      String textValue = jsonNode.getValue().asText();
      if (mappedName != null) {
        returnedNode.put(mappedName, textValue);
      } else {
        returnedNode.put(fieldName, textValue);
      }
    }
    return returnedNode;
  }

  private static Map<String, String> createTemplateMap(String template) {

    Document document;
    try {
      SAXBuilder saxBuilder = new SAXBuilder();
      document = saxBuilder.build(new ClassPathResource(template).getFile());
    } catch (IOException | JDOMException e) {
      throw new DataException(e.getMessage());
    }

    Map<String, String> map = new HashMap<>();
    List<Element> children = document.getRootElement().getChildren();
    for (Element child : children) {
      String openLmisName  = child.getAttributeValue("openlmis-name");
      String vendorName  = child.getAttributeValue("vendor-name");
      map.put(openLmisName,vendorName);
    }

    return map;  //To change body of created methods use File | Settings | File Templates.
  }

  public static String getEventFeed(EventFeedService eventFeedService, String requestURL, int feedNumber, Logger logger, String vendor, String category){
    try {
      Feed feed = eventFeedService.getEventFeed(new URI(requestURL), category, feedNumber);
      try {
        mapFeedBasedOnVendorAndCategory(feed, vendor, category);
      } catch (IOException e) {
        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
      }
      return new WireFeedOutput().outputString(feed);
    } catch (URISyntaxException e) {
      throw new RuntimeException("Bad URI", e);
    } catch (FeedException e) {
      logger.error("error occurred while getting recent feedgenerator", e);
      throw new RuntimeException("Error serializing feed.", e);
    }
  }


}