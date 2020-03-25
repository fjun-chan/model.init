package org.fjun.client.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import javax.annotation.Resource;

import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.fjun.client.HBaseClient;
import org.springframework.stereotype.Component;

@Component
public class HBaseClientImpl implements HBaseClient {

    @Resource
    private Connection connection;

    @Override
    public String getPassword(String key) throws IOException {
        Map<String, String> map = getByRowKey(connection, TableName.valueOf("user"), key, "info",
            Lists.newArrayList("password"), TimeUnit.SECONDS.toMillis(20));
        return map.getOrDefault("password", "");
    }

    private Map<String, String> getByRowKey(Connection connection, TableName tableName, String rowKey,
        String columnFamily, Collection<String> columns, long timeout) throws IOException {
        try (Table table = connection.getTable(tableName)) {
            table.setReadRpcTimeout((int)timeout);
            byte[] cf = Bytes.toBytes(columnFamily);
            Get get = new Get(Bytes.toBytes(rowKey)).addFamily(cf);
            if (CollectionUtils.isNotEmpty(columns)) {
                columns.forEach(c -> get.addColumn(cf, Bytes.toBytes(c)));
            }
            Result result = table.get(get);
            return buildMap(result, cell -> new String(CellUtil.cloneValue(cell)));
        }
    }

    private <V> Map<String, V> buildMap(Result result, Function<Cell, V> exchange) {
        if (result.isEmpty()) {
            return null;
        }
        List<Cell> cells = result.listCells();
        Map<String, V> items = new HashMap<>(cells.size() * 4 / 3);
        for (Cell cell : cells) {
            items.put(new String(CellUtil.cloneQualifier(cell)), exchange.apply(cell));
        }
        return items;
    }
}
