package org.fjun.client;

import java.io.IOException;

public interface HBaseClient {

    String getPassword(String key) throws IOException;
}
