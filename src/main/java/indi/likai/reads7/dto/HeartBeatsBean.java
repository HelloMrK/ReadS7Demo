package indi.likai.reads7.dto;

import com.github.xingshuangs.iot.common.enums.EDataType;
import com.github.xingshuangs.iot.protocol.s7.serializer.S7Variable;

public class HeartBeatsBean {

    @S7Variable(address = "DB301.0", type = EDataType.BOOL)
    public Boolean heartBeat;
}
