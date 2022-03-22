package cn.cocowwy.showdbui.service;

import cn.cocowwy.showdbcore.config.GlobalContext;
import cn.cocowwy.showdbcore.constants.DBEnum;
import cn.cocowwy.showdbcore.entities.IpCount;
import cn.cocowwy.showdbcore.entities.SlaveStatus;
import cn.cocowwy.showdbcore.strategy.MonitorExecuteStrategy;
import cn.cocowwy.showdbcore.strategy.impl.mysql.MySqlExecuteStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Cocowwy
 * @create 2022-03-03-22:08
 */
@Service
public class MonitorService {
    @Autowired
    List<MonitorExecuteStrategy> monitorExecuteStrategy;
    /**
     * 数据源类型所对应的执行策略，适配切换数据源能路由到指定策略
     */
    private static final Map<DBEnum, MonitorExecuteStrategy> MONITOR_STRATEGY = new HashMap(1);

    @PostConstruct
    void init() {
        monitorExecuteStrategy.forEach(s -> {
            if (s instanceof MySqlExecuteStrategy) {
                MONITOR_STRATEGY.put(DBEnum.MySQL, s);
            }
        });
    }

    /**
     * 当前数据源所对应数据库的 IP客户端连接数
     * @return
     */
    public List<IpCount> ipCountInfo() {
        return MONITOR_STRATEGY.get(GlobalContext.getDatabase()).ipConnectCount();
    }

    /**
     * 数据库主从连接信息
     *  -MySQL 仅开启主从之后有返回值
     * @return
     */
    public SlaveStatus masterSlaveInfo() {
        if (!GlobalContext.getDatabase().equals(DBEnum.MySQL)) {
            return null;
        }
        return MONITOR_STRATEGY.get(GlobalContext.getDatabase()).slaveStatus();
    }
}