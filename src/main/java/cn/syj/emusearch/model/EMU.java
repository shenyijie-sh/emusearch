package cn.syj.emusearch.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @program: emusearch
 * @ClassName EMU
 * @description:
 * @author: syj
 * @create: 2021-02-24 15:00
 **/
@Data
@AllArgsConstructor
public class EMU {

    private String model;

    private String number;

    private String bureau;

    private String department;

    private String plant;

    private String description;
}
