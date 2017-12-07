package com.capstone.exporters;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public interface IExportObject {
    String getFileName();
    void setFileName(String name);
    void writeData(OutputStream os, Map<String, String> params, HttpServletRequest request) throws Exception;
}
