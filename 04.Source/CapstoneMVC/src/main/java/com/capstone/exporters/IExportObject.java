package com.capstone.exporters;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public interface IExportObject {
    String getFileName();
    void writeData(OutputStream os, Map<String, String> params) throws IOException;
}
