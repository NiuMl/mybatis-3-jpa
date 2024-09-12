/*
 *    Copyright 2009-2024 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.binding.jpa;

import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/***
 * @author niumengliang Date:2023/12/22 Time:16:35
 */
public class StringUtils {

  private static final Pattern HUMP_PATTERN = Pattern.compile("[A-Z0-9]");

  public static String humpToLine(String str) {
    Matcher matcher = HUMP_PATTERN.matcher(str);
    StringBuilder sb = new StringBuilder();
    while (matcher.find()) {
      matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
    }
    matcher.appendTail(sb);
    if (sb.toString().startsWith("_"))
      return sb.substring(1);
    return sb.toString();
  }

  private static final String NULL_STR = "";

  public static boolean isEmpty(String str) {
    return isNull(str) || NULL_STR.equals(str.trim());
  }

  public static boolean isNull(Object object) {
    return object == null;
  }

  /**
   * 格式化xml
   *
   * @param xmlString
   *          xml内容
   * @param indent
   *          向前缩进多少空格
   * @param ignoreDeclaration
   *          是否忽略描述
   *
   * @return 格式化后的xml
   */
  public static String prettyPrintByTransformer(String xmlString, int indent, boolean ignoreDeclaration) {

    try {
      InputSource src = new InputSource(new StringReader(xmlString));
      Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(src);

      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      transformerFactory.setAttribute("indent-number", indent);
      Transformer transformer = transformerFactory.newTransformer();
      transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
      transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, ignoreDeclaration ? "yes" : "no");
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");

      Writer out = new StringWriter();
      transformer.transform(new DOMSource(document), new StreamResult(out));
      return out.toString();
    } catch (Exception e) {
      throw new RuntimeException("Error occurs when pretty-printing xml:\n" + xmlString, e);
    }
  }

}
