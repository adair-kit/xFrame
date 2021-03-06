package cn.adair.frame.utils.printer;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class PrinterImpl implements PrinterBiz {

    @Override
    public void debug(String message, Object... args) {
        PrinterUtil.log(Log.DEBUG, message, args);
    }

    @Override
    public void error(String message, Object... args) {
        error(null, message, args);
    }

    @Override
    public void error(Throwable throwable, String message, Object... args) {
        if (throwable != null && message != null) {
            message += " : " + throwable.toString();
        }
        if (throwable != null && message == null) {
            message = throwable.toString();
        }
        if (message == null) {
            message = "message/exception 为空！";
        }
        PrinterUtil.log(Log.ERROR, message, args);
    }

    @Override
    public void warn(String message, Object... args) {
        PrinterUtil.log(Log.WARN, message, args);
    }

    @Override
    public void info(String message, Object... args) {
        PrinterUtil.log(Log.INFO, message, args);
    }

    @Override
    public void json(String json) {
        if (TextUtils.isEmpty(json)) {
            debug("json 数据为空！");
            return;
        }
        try {
            String message = "";
            if (json.startsWith("{")) {
                JSONObject jo = new JSONObject(json);
                message = jo.toString(4);
            } else if (json.startsWith("[")) {
                JSONArray ja = new JSONArray(json);
                message = ja.toString(4);
            }
            debug(message);
        } catch (Exception e) {
            error(e.getCause().getMessage() + PrinterSet.LINE_SEPARATOR + json);
        }
    }

    @Override
    public void xml(String xml) {
        if (TextUtils.isEmpty(xml)) {
            debug("xml 数据为空！");
            return;
        }
        try {
            Source xmlInput = new StreamSource(new StringReader(xml));
            StreamResult xmlOutput = new StreamResult(new StringWriter());
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.transform(xmlInput, xmlOutput);
            String message = xmlOutput.getWriter().toString().replaceFirst(">", ">" + PrinterSet.LINE_SEPARATOR);
            debug(message);
        } catch (TransformerException e) {
            error(e.getCause().getMessage() + PrinterSet.LINE_SEPARATOR + xml);
        }
    }

    @Override
    public void map(Map map) {
        if (null == map) {
            debug("map 数据为空！");
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (Object entry : map.entrySet()) {
            stringBuilder.append("[key] → ");
            stringBuilder.append(((Map.Entry) entry).getKey());
            stringBuilder.append(",[value] → ");
            stringBuilder.append(((Map.Entry) entry).getValue());
            stringBuilder.append(PrinterSet.LINE_SEPARATOR);
        }
        debug(stringBuilder.toString());
    }

    @Override
    public void list(List list) {
        if (null == list) {
            debug("list 数据为空！");
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            stringBuilder.append("[" + i + "] → ");
            stringBuilder.append(list.get(i));
            stringBuilder.append(PrinterSet.LINE_SEPARATOR);
        }
        debug(stringBuilder.toString());
    }

    @Override
    public String format() {
        return PrinterUtil.iString.toString();
    }
}
