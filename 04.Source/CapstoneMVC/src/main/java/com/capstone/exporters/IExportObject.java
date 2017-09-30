package com.capstone.exporters;

import java.io.IOException;
import java.io.OutputStream;

public interface IExportObject {
    String getFileName();
    void writeData(OutputStream os) throws IOException;
}
