package com.cta.registry;

import com.cta.CTA;
import com.cta.entity.MissileEntity.WarheadType;
import com.cta.item.MissileItem;
import com.cta.item.ScopeItem;
import com.cta.item.WirelessConnectorItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CTA.MODID);

    public static final DeferredItem<ScopeItem> PANTHER_SCOPE = ITEMS.registerItem("panther_scope", ScopeItem::new);

    public static final DeferredItem<WirelessConnectorItem> WIRELESS_CONNECTOR = ITEMS.registerItem(
            "wireless_connector", WirelessConnectorItem::new);

    private static DeferredItem<MissileItem> registerMissile(String registryName, String missileId, boolean isBomb, WarheadType warheadType) {
        return ITEMS.registerItem(registryName, props -> new MissileItem(props, missileId, isBomb, warheadType));
    }

    // 3M-54 Kalibr
    public static final DeferredItem<MissileItem> KALIBR_3M54_HE = registerMissile("3m_54_kalibr_he", "3m54_kalibr", false, WarheadType.HE);
    public static final DeferredItem<MissileItem> KALIBR_3M54_HEAT = registerMissile("3m_54_kalibr_heat", "3m54_kalibr", false, WarheadType.HEAT);
    public static final DeferredItem<MissileItem> KALIBR_3M54_HEFRAG = registerMissile("3m_54_kalibr_hefrag", "3m54_kalibr", false, WarheadType.HEFRAG);

    // AGM-88 HARM
    public static final DeferredItem<MissileItem> AGM_88_HE = registerMissile("agm_88_he", "agm_88", false, WarheadType.HE);
    public static final DeferredItem<MissileItem> AGM_88_HEAT = registerMissile("agm_88_heat", "agm_88", false, WarheadType.HEAT);
    public static final DeferredItem<MissileItem> AGM_88_HEFRAG = registerMissile("agm_88_hefrag", "agm_88", false, WarheadType.HEFRAG);

    // AIM-9 Sidewinder
    public static final DeferredItem<MissileItem> AIM_9_HE = registerMissile("aim_9_he", "aim_9", false, WarheadType.HE);
    public static final DeferredItem<MissileItem> AIM_9_HEAT = registerMissile("aim_9_heat", "aim_9", false, WarheadType.HEAT);
    public static final DeferredItem<MissileItem> AIM_9_HEFRAG = registerMissile("aim_9_hefrag", "aim_9", false, WarheadType.HEFRAG);

    // APKWS
    public static final DeferredItem<MissileItem> APKWS_HE = registerMissile("apkws_he", "apkws", false, WarheadType.HE);
    public static final DeferredItem<MissileItem> APKWS_HEAT = registerMissile("apkws_heat", "apkws", false, WarheadType.HEAT);
    public static final DeferredItem<MissileItem> APKWS_HEFRAG = registerMissile("apkws_hefrag", "apkws", false, WarheadType.HEFRAG);

    // Hellfire
    public static final DeferredItem<MissileItem> HELLFIRE_HE = registerMissile("hellfire_he", "hellfire", false, WarheadType.HE);
    public static final DeferredItem<MissileItem> HELLFIRE_HEAT = registerMissile("hellfire_heat", "hellfire", false, WarheadType.HEAT);
    public static final DeferredItem<MissileItem> HELLFIRE_HEFRAG = registerMissile("hellfire_hefrag", "hellfire", false, WarheadType.HEFRAG);

    // Hydra-70
    public static final DeferredItem<MissileItem> HYDRA_70_HE = registerMissile("hydra_70_he", "hydra_70", false, WarheadType.HE);
    public static final DeferredItem<MissileItem> HYDRA_70_HEAT = registerMissile("hydra_70_heat", "hydra_70", false, WarheadType.HEAT);
    public static final DeferredItem<MissileItem> HYDRA_70_HEFRAG = registerMissile("hydra_70_hefrag", "hydra_70", false, WarheadType.HEFRAG);

    // Katyusha
    public static final DeferredItem<MissileItem> KATYUSHA_HE = registerMissile("katyusha_he", "katyusha", false, WarheadType.HE);
    public static final DeferredItem<MissileItem> KATYUSHA_HEAT = registerMissile("katyusha_heat", "katyusha", false, WarheadType.HEAT);
    public static final DeferredItem<MissileItem> KATYUSHA_HEFRAG = registerMissile("katyusha_hefrag", "katyusha", false, WarheadType.HEFRAG);

    // R-77
    public static final DeferredItem<MissileItem> R_77_HE = registerMissile("r_77_he", "r_77", false, WarheadType.HE);
    public static final DeferredItem<MissileItem> R_77_HEAT = registerMissile("r_77_heat", "r_77", false, WarheadType.HEAT);
    public static final DeferredItem<MissileItem> R_77_HEFRAG = registerMissile("r_77_hefrag", "r_77", false, WarheadType.HEFRAG);

    // TOW-2
    public static final DeferredItem<MissileItem> TOW_2_HE = registerMissile("tow_2_he", "tow_2", false, WarheadType.HE);
    public static final DeferredItem<MissileItem> TOW_2_HEAT = registerMissile("tow_2_heat", "tow_2", false, WarheadType.HEAT);
    public static final DeferredItem<MissileItem> TOW_2_HEFRAG = registerMissile("tow_2_hefrag", "tow_2", false, WarheadType.HEFRAG);

    // CBU-87
    public static final DeferredItem<MissileItem> CBU_87_HE = registerMissile("cbu_87_he", "cbu_87", true, WarheadType.HE);
    public static final DeferredItem<MissileItem> CBU_87_HEAT = registerMissile("cbu_87_heat", "cbu_87", true, WarheadType.HEAT);
    public static final DeferredItem<MissileItem> CBU_87_HEFRAG = registerMissile("cbu_87_hefrag", "cbu_87", true, WarheadType.HEFRAG);

    // GBU-12
    public static final DeferredItem<MissileItem> GBU_12_HE = registerMissile("gbu_12_he", "gbu_12", true, WarheadType.HE);
    public static final DeferredItem<MissileItem> GBU_12_HEAT = registerMissile("gbu_12_heat", "gbu_12", true, WarheadType.HEAT);
    public static final DeferredItem<MissileItem> GBU_12_HEFRAG = registerMissile("gbu_12_hefrag", "gbu_12", true, WarheadType.HEFRAG);

    // GBU-24
    public static final DeferredItem<MissileItem> GBU_24_HE = registerMissile("gbu_24_he", "gbu_24", true, WarheadType.HE);
    public static final DeferredItem<MissileItem> GBU_24_HEAT = registerMissile("gbu_24_heat", "gbu_24", true, WarheadType.HEAT);
    public static final DeferredItem<MissileItem> GBU_24_HEFRAG = registerMissile("gbu_24_hefrag", "gbu_24", true, WarheadType.HEFRAG);

    // M64
    public static final DeferredItem<MissileItem> M64_HE = registerMissile("m64_he", "m64", true, WarheadType.HE);

    // Rockeye
    public static final DeferredItem<MissileItem> ROCKEYE_HE = registerMissile("rockeye_he", "rockeye", true, WarheadType.HE);
    public static final DeferredItem<MissileItem> ROCKEYE_HEAT = registerMissile("rockeye_heat", "rockeye", true, WarheadType.HEAT);
    public static final DeferredItem<MissileItem> ROCKEYE_HEFRAG = registerMissile("rockeye_hefrag", "rockeye", true, WarheadType.HEFRAG);

    // Snakeye
    public static final DeferredItem<MissileItem> SNAKEYE_HE = registerMissile("snakeye_he", "snakeye", true, WarheadType.HE);
    public static final DeferredItem<MissileItem> SNAKEYE_HEAT = registerMissile("snakeye_heat", "snakeye", true, WarheadType.HEAT);
    public static final DeferredItem<MissileItem> SNAKEYE_HEFRAG = registerMissile("snakeye_hefrag", "snakeye", true, WarheadType.HEFRAG);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
