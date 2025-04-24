package indi.likai.reads7.dto;

import com.github.xingshuangs.iot.common.enums.EDataType;
import com.github.xingshuangs.iot.protocol.s7.serializer.S7Variable;
import lombok.Data;

@Data
public class DetectBean {
    @S7Variable(address = "DB303.160", type = EDataType.STRING, count = 14)
    public String signTime;
    @S7Variable(address = "DB303.176", type = EDataType.STRING, count = 13)
    public String uid;
    @S7Variable(address = "DB303.191", type = EDataType.BYTE)
    public byte[] result;

}
