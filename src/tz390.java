import java.io.File;
import java.lang.reflect.Array;
import java.util.Random;


public  class  tz390 {
   /*****************************************************
	
    z390 portable mainframe assembler and emulator.
	
    Copyright 2005 Automated Software Tools Corporation
	 
    z390 is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    z390 is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with z390; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

    tz390 is the shared table component of z390.

    ****************************************************
    * Maintenance
    ****************************************************
    * 12/13/05 copied from ez390.java and modified
    * 12/15/05 RPI135 tz390 shared tables
    * 12/18/05 RPI142 add init_opcode_name_keys for
    *          use by both mz390 and az390
    * 12/23/05 RPI 127 allow file sfx overrides and add
    *          shared set_pgm_dir_name_type method
    * 12/23/05 RPI 131 MAXFILE default 10 MB
    * 12/31/05 add update_key_index for use by mz390
    * 12/31/05 RPI 150 add OPSYN support 
    * 01/14/06 RPI 168 correct null dir_cur in z390 startup
    * 01/26/06 RPI 172 move options to tz390
    * 01/29/06 RPI 192 share get_char_sdt(sdt) with fixes
    * 02/02/06 RPI 185 add z9 op opcodes to tables
    * 02/08/06 RPI198 support PARM(..) and upper/lower
    *          case parms
    * 02/10/06 RPI 199 add BLX extended mnemonics
    * 02/16/06 RPI 201 support "..." parms with special chars
    *          and strip single quotes from "parm('...')"
    * 02/18/06 RPI 206 add RRF3 format type
    * 02/21/06 RPI 208 use z390_abort to sync term.
    * 03/13/06 RPI 226 fix options in double quotes
    ********************************************************
    * Shared z390 tables
    *****************************************************/
	/*
	 * shared version id 
	 */
	// dsh - change version for every release and ptf
	// dsh - change dcb_id_ver for dcb field changes
    String version    = "V1.0.13";  //dsh
	String dcb_id_ver = "DCBV1001"; //dsh
	/*
	 * global options 
	 */ 
	boolean z390_abort   = false;  // global abort request
    boolean opt_amode24  = false;  // link to run amode24
    boolean opt_amode31  = true;   // link to run amode31
    boolean opt_ascii    = false; // use ascii vs ebcdic
    boolean opt_con      = true;  // log msgs to console
    boolean opt_dump     = false; // only indicative dump on abend unless on
    boolean opt_guam     = false;  // use gz390 GUAM GUI access method interface
    boolean opt_objhex   = false;  // generate ascii hex obj records (lz390 accepts bin or hex)
    boolean opt_list     = true; // generate LOG file
    boolean opt_listcall = true;  // list macro calls
    boolean opt_listfile = true;  // list each file path
    boolean opt_mfc      = true;   // mainframe compatiblity
    String  opt_parm     = "";      // user parm string for ez390 (mapped to R1 > cvt_exec_parm)
    boolean opt_regs     = false; // show registers on trace
    boolean opt_rmode24  = true;   // link to load below line
    boolean opt_rmode31  = false;  // link to load above line
    boolean opt_stats    = true;  // show statistics on LOG file
    String  opt_sysparm  = "";    // user parm string for mz390
    boolean opt_test     = false; // invoke interactive test cmds
    boolean opt_time     = true;  // abend 422 if out of time TIME (sec)
    boolean opt_timing   = true;  // display current date, time, rate
    boolean opt_trace    = false; // trace pz390 instructions to LOG
    boolean opt_tracea   = false;  // trace az390
    boolean opt_traceall = false; // trace all details
    boolean opt_tracel   = false;  // trace lz390
    boolean opt_tracem   = false; // trace mz390
    boolean opt_tracemem = false; // trace memory FQE updates to LOG
    boolean opt_trap     = true;  // trap exceptions as 0C5
    boolean opt_xref     = true;   // cross reference symbols
    String  cmd_parms = " "; // all options from command
    String  test_ddname = null;
    char    z390_amode31 = 'T';
    char    z390_rmode31 = 'F';
    /*
	 * global limits with option overrides
	 */
	int    max_errors        = 100;     // ERR(100) max errors before abort
	long   max_file = 50 << 20;         // max file output 
	long   max_time_seconds  = 15;      // TIME(15)max elapsed time - override time(sec)
    int    monitor_wait = 300;          // fix interval in milliseconds
    int    max_mem           = 1;       // MEM(1)  MB memory default (see mem(mb) override)
    /*
	 * shared pgm dir, name, type and associated dirs
	 */
	String dir_cur = null; // default current dir
	String pgm_dir  = null; // from first parm else dir_cur
	String pgm_name = null; // from first parm else abort
	String pgm_type = null; // from first parm override if mlc else def.
    String dir_390 = null; // SYS390() load module
    String dir_bal = null; // SYSBAL() az390 source input
    String dir_cpy = null; // SYSCPY() mz390 copybook lib
    String dir_dat = null; // SYSDAT() mz390 AREAD extended option
    String dir_log = null; // SYSLOG() ez390 log
    String dir_lst = null; // SYSLST() lz390 listing 
    String dir_mac = null; // SYSMAC() mz390 macro lib
    String dir_mlc = null; // SYSMLC() mz390 source input
    String dir_pch = null; // SYSPCH() mz390 punch output dir
    String dir_prn = null; // SYSPRN() az390 listing
    String dir_obj = null; // SYSOBJ() lz390 object lib
    boolean opsyn_name_change = false;
	/*
     * ASCII and EBCDIC printable character tables
     */
        int sdt_char_int = 0; // RPI 192 shared character sdt
        String ascii_table = 
        "................" + //00
        "................" + //10
        " !" + '"' + "#$%&'()*+,-./" + //20 with "
        "0123456789:;<=>?" + //30
        "@ABCDEFGHIJKLMNO" + //40
        "PQRSTUVWXYZ[\\]^_" + //50
        "`abcdefghijklmno" + //60
        "pqrstuvwxyz{|}~." + //70
        "................" + //80
        "................" + //90
        "................" + //A0
        "................" + //B0
        "................" + //C0
        "................" + //D0
        "................" + //E0
        "................";  //F0
        String ebcdic_table =
        "................" + //00
        "................" + //10
        "................" + //20
        "................" + //30
        " ...........<(+|" + //40
        "&.........!$*);." + //50
        "-/.........,%_>?" + //60
        ".........`:#@'=" + '"' + //70 with "
        ".abcdefghi......" + //80
        ".jklmnopqr......" + //90
        ".~stuvwxyz......" + //A0
        "^.........[]...." + //B0
        "{ABCDEFGHI......" + //C0
        "}JKLMNOPQR......" + //D0
        "\\.STUVWXYZ......" + //E0 with \
        "0123456789......";   //F0
        byte[] ascii_to_ebcdic = new byte[256];
        String ascii_to_ebcdic_hex = 
                        "00010203372D2E2F1605250B0C0D0E0F" + //00 ................ 
                        "101112003C3D322618193F2722003500" + //20 ................ 
                        "405A7F7B5B6C507D4D5D5C4E6B604B61" + //20  !"#$%&'()*+,-./ 
                        "F0F1F2F3F4F5F6F7F8F97A5E4C7E6E6F" + //30 0123456789:;<=>? 
                        "7CC1C2C3C4C5C6C7C8C9D1D2D3D4D5D6" + //40 @ABCDEFGHIJKLMNO  
                        "D7D8D9E2E3E4E5E6E7E8E9ADE0BD5F6D" + //50 PQRSTUVWXYZ.\.._  
                        "79818283848586878889919293949596" + //60 `abcdefghijklmno 
                        "979899A2A3A4A5A6A7A8A98B4F9BA107" + //70 pqrstuvwxyz.|.~. 
                        "00010203372D2E2F1605250B0C0D0E0F" + //80 ................  
                        "101112003C3D322618193F2722003500" + //90 ................ 
                        "405A7F7B5B6C507D4D5D5C4E6B604B61" + //A0  !"#$%&'()*+,-./ 
                        "F0F1F2F3F4F5F6F7F8F97A5E4C7E6E6F" + //B0 0123456789:;<=>? 
                        "7CC1C2C3C4C5C6C7C8C9D1D2D3D4D5D6" + //C0 @ABCDEFGHIJKLMNO 
                        "D7D8D9E2E3E4E5E6E7E8E9ADE0BD5F6D" + //D0 PQRSTUVWXYZ.\.._ 
                        "79818283848586878889919293949596" + //E0 `abcdefghijklmno 
                        "979899A2A3A4A5A6A7A8A98B4F9BA107"   //F0 pqrstuvwxyz.|.~. 
        ;
        byte[] ebcdic_to_ascii = new byte[256];
        String ebcdic_to_ascii_hex = 
                        "000102030009007F0000000B0C0D0E0F" + //00 ................ 
                        "10111200000008001819000000000000" + //10 ................ 
                        "00001C00000A171B0000000000050607" + //20 ................ 
                        "00001600001E0004000000001415001A" + //30 ................ 
                        "20000000000000000000002E3C282B7C" + //40  ...........<(+| 
                        "2600000000000000000021242A293B5E" + //50 &.........!$*);^ 
                        "2D2F0000000000000000002C255F3E3F" + //60 -/.........,%_>? 
                        "000000000000000000603A2340273D22" + //70 .........`:#@'=" 
                        "00616263646566676869007B00000000" + //80 .abcdefghi.{.... 
                        "006A6B6C6D6E6F707172007D00000000" + //90 .jklmnopqr.}.... 
                        "007E737475767778797A0000005B0000" + //A0 .~stuvwxyz...[.. 
                        "000000000000000000000000005D0000" + //B0 .............].. 
                        "00414243444546474849000000000000" + //C0 .ABCDEFGHI...... 
                        "004A4B4C4D4E4F505152000000000000" + //D0 .JKLMNOPQR...... 
                        "5C00535455565758595A000000000000" + //E0 \.STUVWXYZ...... 
                        "30313233343536373839000000000000";  //F0 0123456789......  
  /*
   * opcode tables for trace
   */
      String[] op_name = {
    		   "*",        // 00 comments
		       "PR",       // 10 "0101" "PR" "E" 1
		       "UPT",      // 20 "0102" "UPT" "E" 1
		       "PTFF",     //    "0104" "PTFF" "E" 1 Z9-1
		       "SCKPF",    // 30 "0107" "SCKPF" "E" 1
		       "TAM",      // 40 "010B" "TAM" "E" 1
		       "SAM24",    // 50 "010C" "SAM24" "E" 1
		       "SAM31",    // 60 "010D" "SAM31" "E" 1
		       "SAM64",    // 70 "010E" "SAM64" "E" 1
		       "TRAP2",    // 80 "01FF" "TRAP2" "E" 1
		       "SPM",      // 90 "04" "SPM" "RR" 2
		       "BALR",     // 100 "05" "BALR" "RR" 2
		       "BCTR",     // 110 "06" "BCTR" "RR" 2
		       "BCR",      // 120 "07" "BCR" "RR" 2
		       "BR",       // 130 "07F" "BR" "BRX" 3
		       "NOPR",     // 140 "070" "NOPR" "BRX" 3
		       "BHR",      // 150 "072" "BHR" "BRX" 3
		       "BLR",      // 160 "074" "BLR" "BRX" 3
		       "BER",      // 170 "078" "BER" "BRX" 3
		       "BNHR",     // 180 "07D" "BNHR" "BRX" 3
		       "BNLR",     // 190 "07B" "BNLR" "BRX" 3
		       "BNER",     // 200 "077" "BNER" "BRX" 3
		       "BPR",      // 210 "072" "BPR" "BRX" 3
		       "BOR",      // 220 "071" "BOR" "BRX" 3
		       "BMR",      // 230 "074" "BMR" "BRX" 3
		       "BZR",      // 240 "078" "BZR" "BRX" 3
		       "BNPR",     // 250 "07D" "BNPR" "BRX" 3
		       "BNMR",     // 260 "07B" "BNMR" "BRX" 3
		       "BNZR",     // 270 "077" "BNZR" "BRX" 3
		       "BNOR",     // 280 "07E" "BNOR" "BRX" 3
		       "SVC",      // 290 "0A" "SVC" "I" 4
		       "BSM",      // 300 "0B" "BSM" "RR" 2
		       "BASSM",    // 310 "0C" "BASSM" "RR" 2
		       "BASR",     // 320 "0D" "BASR" "RR" 2
		       "MVCL",     // 330 "0E" "MVCL" "RR" 2
		       "CLCL",     // 340 "0F" "CLCL" "RR" 2
		       "LPR",      // 350 "10" "LPR" "RR" 2
		       "LNR",      // 360 "11" "LNR" "RR" 2
		       "LTR",      // 370 "12" "LTR" "RR" 2
		       "LCR",      // 380 "13" "LCR" "RR" 2
		       "NR",       // 390 "14" "NR" "RR" 2
		       "CLR",      // 400 "15" "CLR" "RR" 2
		       "OR",       // 410 "16" "OR" "RR" 2
		       "XR",       // 420 "17" "XR" "RR" 2
		       "LR",       // 430 "18" "LR" "RR" 2
		       "CR",       // 440 "19" "CR" "RR" 2
		       "AR",       // 450 "1A" "AR" "RR" 2
		       "SR",       // 460 "1B" "SR" "RR" 2
		       "MR",       // 470 "1C" "MR" "RR" 2
		       "DR",       // 480 "1D" "DR" "RR" 2
		       "ALR",      // 490 "1E" "ALR" "RR" 2
		       "SLR",      // 500 "1F" "SLR" "RR" 2
		       "LPDR",     // 510 "20" "LPDR" "RR" 2
		       "LNDR",     // 520 "21" "LNDR" "RR" 2
		       "LTDR",     // 530 "22" "LTDR" "RR" 2
		       "LCDR",     // 540 "23" "LCDR" "RR" 2
		       "HDR",      // 550 "24" "HDR" "RR" 2
		       "LDXR",     // 560 "25" "LDXR" "RR" 2
		       "LRDR",     // 570 "25" "LRDR" "RR" 2
		       "MXR",      // 580 "26" "MXR" "RR" 2
		       "MXDR",     // 590 "27" "MXDR" "RR" 2
		       "LDR",      // 600 "28" "LDR" "RR" 2
		       "CDR",      // 610 "29" "CDR" "RR" 2
		       "ADR",      // 620 "2A" "ADR" "RR" 2
		       "SDR",      // 630 "2B" "SDR" "RR" 2
		       "MDR",      // 640 "2C" "MDR" "RR" 2
		       "DDR",      // 650 "2D" "DDR" "RR" 2
		       "AWR",      // 660 "2E" "AWR" "RR" 2
		       "SWR",      // 670 "2F" "SWR" "RR" 2
		       "LPER",     // 680 "30" "LPER" "RR" 2
		       "LNER",     // 690 "31" "LNER" "RR" 2
		       "LTER",     // 700 "32" "LTER" "RR" 2
		       "LCER",     // 710 "33" "LCER" "RR" 2
		       "HER",      // 720 "34" "HER" "RR" 2
		       "LEDR",     // 730 "35" "LEDR" "RR" 2
		       "LRER",     // 740 "35" "LRER" "RR" 2
		       "AXR",      // 750 "36" "AXR" "RR" 2
		       "SXR",      // 760 "37" "SXR" "RR" 2
		       "LER",      // 770 "38" "LER" "RR" 2
		       "CER",      // 780 "39" "CER" "RR" 2
		       "AER",      // 790 "3A" "AER" "RR" 2
		       "SER",      // 800 "3B" "SER" "RR" 2
		       "MDER",     // 810 "3C" "MDER" "RR" 2
		       "MER",      // 820 "3C" "MER" "RR" 2
		       "DER",      // 830 "3D" "DER" "RR" 2
		       "AUR",      // 840 "3E" "AUR" "RR" 2
		       "SUR",      // 850 "3F" "SUR" "RR" 2
		       "STH",      // 860 "40" "STH" "RX" 5
		       "LA",       // 870 "41" "LA" "RX" 5
		       "STC",      // 880 "42" "STC" "RX" 5
		       "IC",       // 890 "43" "IC" "RX" 5
		       "EX",       // 900 "44" "EX" "RX" 5
		       "BAL",      // 910 "45" "BAL" "RX" 5
		       "BCT",      // 920 "46" "BCT" "RX" 5
		       "BC",       // 930 "47" "BC" "RX" 5
		       "B",        // 940 "47F" "B" "BCX" 6
		       "NOP",      // 950 "470" "NOP" "BCX" 6
		       "BH",       // 960 "472" "BH" "BCX" 6
		       "BL",       // 970 "474" "BL" "BCX" 6
		       "BE",       // 980 "478" "BE" "BCX" 6
		       "BNH",      // 990 "47D" "BNH" "BCX" 6
		       "BNL",      // 1000 "47B" "BNL" "BCX" 6
		       "BNE",      // 1010 "477" "BNE" "BCX" 6
		       "BP",       // 1020 "472" "BP" "BCX" 6
		       "BO",       // 1030 "471" "BO" "BCX" 6
		       "BM",       // 1040 "474" "BM" "BCX" 6
		       "BZ",       // 1050 "478" "BZ" "BCX" 6
		       "BNP",      // 1060 "47D" "BNP" "BCX" 6
		       "BNM",      // 1070 "47B" "BNM" "BCX" 6
		       "BNZ",      // 1080 "477" "BNZ" "BCX" 6
		       "BNO",      // 1090 "47E" "BNO" "BCX" 6
		       "LH",       // 1100 "48" "LH" "RX" 5
		       "CH",       // 1110 "49" "CH" "RX" 5
		       "AH",       // 1120 "4A" "AH" "RX" 5
		       "SH",       // 1130 "4B" "SH" "RX" 5
		       "MH",       // 1140 "4C" "MH" "RX" 5
		       "BAS",      // 1150 "4D" "BAS" "RX" 5
		       "CVD",      // 1160 "4E" "CVD" "RX" 5
		       "CVB",      // 1170 "4F" "CVB" "RX" 5
		       "ST",       // 1180 "50" "ST" "RX" 5
		       "LAE",      // 1190 "51" "LAE" "RX" 5
		       "N",        // 1200 "54" "N" "RX" 5
		       "CL",       // 1210 "55" "CL" "RX" 5
		       "O",        // 1220 "56" "O" "RX" 5
		       "X",        // 1230 "57" "X" "RX" 5
		       "L",        // 1240 "58" "L" "RX" 5
		       "C",        // 1250 "59" "C" "RX" 5
		       "A",        // 1260 "5A" "A" "RX" 5
		       "S",        // 1270 "5B" "S" "RX" 5
		       "M",        // 1280 "5C" "M" "RX" 5
		       "D",        // 1290 "5D" "D" "RX" 5
		       "AL",       // 1300 "5E" "AL" "RX" 5
		       "SL",       // 1310 "5F" "SL" "RX" 5
		       "STD",      // 1320 "60" "STD" "RX" 5
		       "MXD",      // 1330 "67" "MXD" "RX" 5
		       "LD",       // 1340 "68" "LD" "RX" 5
		       "CD",       // 1350 "69" "CD" "RX" 5
		       "AD",       // 1360 "6A" "AD" "RX" 5
		       "SD",       // 1370 "6B" "SD" "RX" 5
		       "MD",       // 1380 "6C" "MD" "RX" 5
		       "DD",       // 1390 "6D" "DD" "RX" 5
		       "AW",       // 1400 "6E" "AW" "RX" 5
		       "SW",       // 1410 "6F" "SW" "RX" 5
		       "STE",      // 1420 "70" "STE" "RX" 5
		       "MS",       // 1430 "71" "MS" "RX" 5
		       "LE",       // 1440 "78" "LE" "RX" 5
		       "CE",       // 1450 "79" "CE" "RX" 5
		       "AE",       // 1460 "7A" "AE" "RX" 5
		       "SE",       // 1470 "7B" "SE" "RX" 5
		       "MDE",      // 1480 "7C" "MDE" "RX" 5
		       "ME",       // 1490 "7C" "ME" "RX" 5
		       "DE",       // 1500 "7D" "DE" "RX" 5
		       "AU",       // 1510 "7E" "AU" "RX" 5
		       "SU",       // 1520 "7F" "SU" "RX" 5
		       "SSM",      // 1530 "8000" "SSM" "S" 7
		       "LPSW",     // 1540 "8200" "LPSW" "S" 7
		       "DIAGNOSE", // 1550 "83" "DIAGNOSE" "DM" 8
		       "BRXH",     // 1560 "84" "BRXH" "RSI" 9
		       "JXH",      // 1570 "84" "JXH" "RSI" 9
		       "BRXLE",    // 1580 "85" "BRXLE" "RSI" 9
		       "JXLE",     // 1590 "85" "JXLE" "RSI" 9
		       "BXH",      // 1600 "86" "BXH" "RS" 10
		       "BXLE",     // 1610 "87" "BXLE" "RS" 10
		       "SRL",      // 1620 "88" "SRL" "RS" 10
		       "SLL",      // 1630 "89" "SLL" "RS" 10
		       "SRA",      // 1640 "8A" "SRA" "RS" 10
		       "SLA",      // 1650 "8B" "SLA" "RS" 10
		       "SRDL",     // 1660 "8C" "SRDL" "RS" 10
		       "SLDL",     // 1670 "8D" "SLDL" "RS" 10
		       "SRDA",     // 1680 "8E" "SRDA" "RS" 10
		       "SLDA",     // 1690 "8F" "SLDA" "RS" 10
		       "STM",      // 1700 "90" "STM" "RS" 10
		       "TM",       // 1710 "91" "TM" "SI" 11
		       "MVI",      // 1720 "92" "MVI" "SI" 11
		       "TS",       // 1730 "9300" "TS" "S" 7
		       "NI",       // 1740 "94" "NI" "SI" 11
		       "CLI",      // 1750 "95" "CLI" "SI" 11
		       "OI",       // 1760 "96" "OI" "SI" 11
		       "XI",       // 1770 "97" "XI" "SI" 11
		       "LM",       // 1780 "98" "LM" "RS" 10
		       "TRACE",    // 1790 "99" "TRACE" "RS" 10
		       "LAM",      // 1800 "9A" "LAM" "RS" 10
		       "STAM",     // 1810 "9B" "STAM" "RS" 10
		       "IIHH",     // 1820 "A50" "IIHH" "RI" 12
		       "IIHL",     // 1830 "A51" "IIHL" "RI" 12
		       "IILH",     // 1840 "A52" "IILH" "RI" 12
		       "IILL",     // 1850 "A53" "IILL" "RI" 12
		       "NIHH",     // 1860 "A54" "NIHH" "RI" 12
		       "NIHL",     // 1870 "A55" "NIHL" "RI" 12
		       "NILH",     // 1880 "A56" "NILH" "RI" 12
		       "NILL",     // 1890 "A57" "NILL" "RI" 12
		       "OIHH",     // 1900 "A58" "OIHH" "RI" 12
		       "OIHL",     // 1910 "A59" "OIHL" "RI" 12
		       "OILH",     // 1920 "A5A" "OILH" "RI" 12
		       "OILL",     // 1930 "A5B" "OILL" "RI" 12
		       "LLIHH",    // 1940 "A5C" "LLIHH" "RI" 12
		       "LLIHL",    // 1950 "A5D" "LLIHL" "RI" 12
		       "LLILH",    // 1960 "A5E" "LLILH" "RI" 12
		       "LLILL",    // 1970 "A5F" "LLILL" "RI" 12
		       "TMLH",     // 1980 "A70" "TMLH" "RI" 12
		       "TMH",      // 1990 "A70" "TMH" "RI" 12
		       "TMLL",     // 2000 "A71" "TMLL" "RI" 12
		       "TML",      // 2010 "A71" "TML" "RI" 12
		       "TMHH",     // 2020 "A72" "TMHH" "RI" 12
		       "TMHL",     // 2030 "A73" "TMHL" "RI" 12
		       "BRC",      // 2040 "A74" "BRC" "RI" 12
		       "J",        // 2050 "A74F" "J" "BRC" 13
		       "JNOP",     // 2060 "A740" "JNOP" "BRC" 13
		       "BRU",      // 2070 "A74F" "BRU" "BRC" 13
		       "BRH",      // 2080 "A742" "BRH" "BRC" 13
		       "BRL",      // 2090 "A744" "BRL" "BRC" 13
		       "BRE",      // 2100 "A748" "BRE" "BRC" 13
		       "BRNH",     // 2110 "A74D" "BRNH" "BRC" 13
		       "BRNL",     // 2120 "A74B" "BRNL" "BRC" 13
		       "BRNE",     // 2130 "A747" "BRNE" "BRC" 13
		       "BRP",      // 2140 "A742" "BRP" "BRC" 13
		       "BRM",      // 2150 "A744" "BRM" "BRC" 13
		       "BRZ",      // 2160 "A748" "BRZ" "BRC" 13
		       "BRO",      // 2170 "A741" "BRO" "BRC" 13
		       "BRNP",     // 2180 "A74D" "BRNP" "BRC" 13
		       "BRNM",     // 2190 "A74B" "BRNM" "BRC" 13
		       "BRNZ",     // 2200 "A747" "BRNZ" "BRC" 13
		       "BRNO",     // 2210 "A74E" "BRNO" "BRC" 13
		       "JH",       // 2220 "A742" "JH" "BRC" 13
		       "JL",       // 2230 "A744" "JL" "BRC" 13
		       "JE",       // 2240 "A748" "JE" "BRC" 13
		       "JNH",      // 2250 "A74D" "JNH" "BRC" 13
		       "JNL",      // 2260 "A74B" "JNL" "BRC" 13
		       "JNE",      // 2270 "A747" "JNE" "BRC" 13
		       "JP",       // 2280 "A742" "JP" "BRC" 13
		       "JM",       // 2290 "A744" "JM" "BRC" 13
		       "JZ",       // 2300 "A748" "JZ" "BRC" 13
		       "JO",       // 2310 "A741" "JO" "BRC" 13
		       "JNP",      // 2320 "A74D" "JNP" "BRC" 13
		       "JNM",      // 2330 "A74B" "JNM" "BRC" 13
		       "JNZ",      // 2340 "A747" "JNZ" "BRC" 13
		       "JNO",      // 2350 "A74E" "JNO" "BRC" 13
		       "BRAS",     // 2360 "A75" "BRAS" "RI" 12
		       "JAS",      // 2370 "A75" "JAS" "RI" 12
		       "BRCT",     // 2380 "A76" "BRCT" "RI" 12
		       "JCT",      // 2390 "A76" "JCT" "RI" 12
		       "BRCTG",    // 2400 "A77" "BRCTG" "RI" 12
		       "JCTG",     // 2410 "A77" "JCTG" "RI" 12
		       "LHI",      // 2420 "A78" "LHI" "RI" 12
		       "LGHI",     // 2430 "A79" "LGHI" "RI" 12
		       "AHI",      // 2440 "A7A" "AHI" "RI" 12
		       "AGHI",     // 2450 "A7B" "AGHI" "RI" 12
		       "MHI",      // 2460 "A7C" "MHI" "RI" 12
		       "MGHI",     // 2470 "A7D" "MGHI" "RI" 12
		       "CHI",      // 2480 "A7E" "CHI" "RI" 12
		       "CGHI",     // 2490 "A7F" "CGHI" "RI" 12
		       "MVCLE",    // 2500 "A8" "MVCLE" "RS" 10
		       "CLCLE",    // 2510 "A9" "CLCLE" "RS" 10
		       "STNSM",    // 2520 "AC" "STNSM" "SI" 11
		       "STOSM",    // 2530 "AD" "STOSM" "SI" 11
		       "SIGP",     // 2540 "AE" "SIGP" "RS" 10
		       "MC",       // 2550 "AF" "MC" "SI" 11
		       "LRA",      // 2560 "B1" "LRA" "RX" 5
		       "STIDP",    // 2570 "B202" "STIDP" "S" 7
		       "SCK",      // 2580 "B204" "SCK" "S" 7
		       "STCK",     // 2590 "B205" "STCK" "S" 7
		       "SCKC",     // 2600 "B206" "SCKC" "S" 7
		       "STCKC",    // 2610 "B207" "STCKC" "S" 7
		       "SPT",      // 2620 "B208" "SPT" "S" 7
		       "STPT",     // 2630 "B209" "STPT" "S" 7
		       "SPKA",     // 2640 "B20A" "SPKA" "S" 7
		       "IPK",      // 2650 "B20B" "IPK" "S" 7
		       "PTLB",     // 2660 "B20D" "PTLB" "S" 7
		       "SPX",      // 2670 "B210" "SPX" "S" 7
		       "STPX",     // 2680 "B211" "STPX" "S" 7
		       "STAP",     // 2690 "B212" "STAP" "S" 7
		       "PC",       // 2700 "B218" "PC" "S" 7
		       "SAC",      // 2710 "B219" "SAC" "S" 7
		       "CFC",      // 2720 "B21A" "CFC" "S" 7
		       "IPTE",     // 2730 "B221" "IPTE" "RRE" 14
		       "IPM",      // 2740 "B222" "IPM" "RRE" 14
		       "IVSK",     // 2750 "B223" "IVSK" "RRE" 14
		       "IAC",      // 2760 "B224" "IAC" "RRE" 14
		       "SSAR",     // 2770 "B225" "SSAR" "RRE" 14
		       "EPAR",     // 2780 "B226" "EPAR" "RRE" 14
		       "ESAR",     // 2790 "B227" "ESAR" "RRE" 14
		       "PT",       // 2800 "B228" "PT" "RRE" 14
		       "ISKE",     // 2810 "B229" "ISKE" "RRE" 14
		       "RRBE",     // 2820 "B22A" "RRBE" "RRE" 14
		       "SSKE",     // 2830 "B22B" "SSKE" "RRE" 14
		       "TB",       // 2840 "B22C" "TB" "RRE" 14
		       "DXR",      // 2850 "B22D" "DXR" "RRE" 14
		       "PGIN",     // 2860 "B22E" "PGIN" "RRE" 14
		       "PGOUT",    // 2870 "B22F" "PGOUT" "RRE" 14
		       "CSCH",     // 2880 "B230" "CSCH" "S" 7
		       "HSCH",     // 2890 "B231" "HSCH" "S" 7
		       "MSCH",     // 2900 "B232" "MSCH" "S" 7
		       "SSCH",     // 2910 "B233" "SSCH" "S" 7
		       "STSCH",    // 2920 "B234" "STSCH" "S" 7
		       "TSCH",     // 2930 "B235" "TSCH" "S" 7
		       "TPI",      // 2940 "B236" "TPI" "S" 7
		       "SAL",      // 2950 "B237" "SAL" "S" 7
		       "RSCH",     // 2960 "B238" "RSCH" "S" 7
		       "STCRW",    // 2970 "B239" "STCRW" "S" 7
		       "STCPS",    // 2980 "B23A" "STCPS" "S" 7
		       "RCHP",     // 2990 "B23B" "RCHP" "S" 7
		       "SCHM",     // 3000 "B23C" "SCHM" "S" 7
		       "BAKR",     // 3010 "B240" "BAKR" "RRE" 14
		       "CKSM",     // 3020 "B241" "CKSM" "RRE" 14
		       "SQDR",     // 3030 "B244" "SQDR" "RRE" 14
		       "SQER",     // 3040 "B245" "SQER" "RRE" 14
		       "STURA",    // 3050 "B246" "STURA" "RRE" 14
		       "MSTA",     // 3060 "B247" "MSTA" "RRE" 14
		       "PALB",     // 3070 "B248" "PALB" "RRE" 14
		       "EREG",     // 3080 "B249" "EREG" "RRE" 14
		       "ESTA",     // 3090 "B24A" "ESTA" "RRE" 14
		       "LURA",     // 3100 "B24B" "LURA" "RRE" 14
		       "TAR",      // 3110 "B24C" "TAR" "RRE" 14
		       "CPYA",     // 3120 "B24D" "CPYA" "RRE" 14
		       "SAR",      // 3130 "B24E" "SAR" "RRE" 14
		       "EAR",      // 3140 "B24F" "EAR" "RRE" 14
		       "CSP",      // 3150 "B250" "CSP" "RRE" 14
		       "MSR",      // 3160 "B252" "MSR" "RRE" 14
		       "MVPG",     // 3170 "B254" "MVPG" "RRE" 14
		       "MVST",     // 3180 "B255" "MVST" "RRE" 14
		       "CUSE",     // 3190 "B257" "CUSE" "RRE" 14
		       "BSG",      // 3200 "B258" "BSG" "RRE" 14
		       "BSA",      // 3210 "B25A" "BSA" "RRE" 14
		       "CLST",     // 3220 "B25D" "CLST" "RRE" 14
		       "SRST",     // 3230 "B25E" "SRST" "RRE" 14
		       "CMPSC",    // 3240 "B263" "CMPSC" "RRE" 14
		       "XSCH",     // 3250 "B276" "XSCH" "S" 7
		       "RP",       // 3260 "B277" "RP" "S" 7
		       "STCKE",    // 3270 "B278" "STCKE" "S" 7
		       "SACF",     // 3280 "B279" "SACF" "S" 7
		       "STCKF",    //      "B27C" "STCKF" "S" 7 Z9-2
		       "STSI",     // 3290 "B27D" "STSI" "S" 7
		       "SRNM",     // 3300 "B299" "SRNM" "S" 7
		       "STFPC",    // 3310 "B29C" "STFPC" "S" 7
		       "LFPC",     // 3320 "B29D" "LFPC" "S" 7
		       "TRE",      // 3330 "B2A5" "TRE" "RRE" 14
		       "CUUTF",    // 3340 "B2A6" "CUUTF" "RRE" 14
		       "CU21",     // 3350 "B2A6" "CU21" "RRE" 14
		       "CUTFU",    // 3360 "B2A7" "CUTFU" "RRE" 14
		       "CU12",     // 3370 "B2A7" "CU12" "RRE" 14
		       "STFLE",    //      "B2B0" "STFLE" "S" 7 Z9-3
		       "STFL",     // 3380 "B2B1" "STFL" "S" 7
		       "LPSWE",    // 3390 "B2B2" "LPSWE" "S" 7
		       "TRAP4",    // 3400 "B2FF" "TRAP4" "S" 7
		       "LPEBR",    // 3410 "B300" "LPEBR" "RRE" 14
		       "LNEBR",    // 3420 "B301" "LNEBR" "RRE" 14
		       "LTEBR",    // 3430 "B302" "LTEBR" "RRE" 14
		       "LCEBR",    // 3440 "B303" "LCEBR" "RRE" 14
		       "LDEBR",    // 3450 "B304" "LDEBR" "RRE" 14
		       "LXDBR",    // 3460 "B305" "LXDBR" "RRE" 14
		       "LXEBR",    // 3470 "B306" "LXEBR" "RRE" 14
		       "MXDBR",    // 3480 "B307" "MXDBR" "RRE" 14
		       "KEBR",     // 3490 "B308" "KEBR" "RRE" 14
		       "CEBR",     // 3500 "B309" "CEBR" "RRE" 14
		       "AEBR",     // 3510 "B30A" "AEBR" "RRE" 14
		       "SEBR",     // 3520 "B30B" "SEBR" "RRE" 14
		       "MDEBR",    // 3530 "B30C" "MDEBR" "RRE" 14
		       "DEBR",     // 3540 "B30D" "DEBR" "RRE" 14
		       "MAEBR",    // 3550 "B30E" "MAEBR" "RRF1" 15
		       "MSEBR",    // 3560 "B30F" "MSEBR" "RRF1" 15
		       "LPDBR",    // 3570 "B310" "LPDBR" "RRE" 14
		       "LNDBR",    // 3580 "B311" "LNDBR" "RRE" 14
		       "LTDBR",    // 3590 "B312" "LTDBR" "RRE" 14
		       "LCDBR",    // 3600 "B313" "LCDBR" "RRE" 14
		       "SQEBR",    // 3610 "B314" "SQEBR" "RRE" 14
		       "SQDBR",    // 3620 "B315" "SQDBR" "RRE" 14
		       "SQXBR",    // 3630 "B316" "SQXBR" "RRE" 14
		       "MEEBR",    // 3640 "B317" "MEEBR" "RRE" 14
		       "KDBR",     // 3650 "B318" "KDBR" "RRE" 14
		       "CDBR",     // 3660 "B319" "CDBR" "RRE" 14
		       "ADBR",     // 3670 "B31A" "ADBR" "RRE" 14
		       "SDBR",     // 3680 "B31B" "SDBR" "RRE" 14
		       "MDBR",     // 3690 "B31C" "MDBR" "RRE" 14
		       "DDBR",     // 3700 "B31D" "DDBR" "RRE" 14
		       "MADBR",    // 3710 "B31E" "MADBR" "RRF1" 15
		       "MSDBR",    // 3720 "B31F" "MSDBR" "RRF1" 15
		       "LDER",     // 3730 "B324" "LDER" "RRE" 14
		       "LXDR",     // 3740 "B325" "LXDR" "RRE" 14
		       "LXER",     // 3750 "B326" "LXER" "RRE" 14
		       "MAER",     // 3760 "B32E" "MAER" "RRF1" 15
		       "MSER",     // 3770 "B32F" "MSER" "RRF1" 15
		       "SQXR",     // 3780 "B336" "SQXR" "RRE" 14
		       "MEER",     // 3790 "B337" "MEER" "RRE" 14
		       "MAYLR",    //      "B338" "MAYLR" "RRF1" 15 Z9-4
		       "MYLR",     //      "B339" "MYLR" "RRF1" 15 Z9-5
		       "MAYR",     //      "B33A" "MAYR" "RRF1" 15 Z9-6
		       "MYR",      //      "B33B" "MYR" "RRF1" 15 Z9-7
		       "MAYHR",    //      "B33C" "MAYHR" "RRF1" 15 Z9-8
		       "MYHR",     //      "B33D" "MYHR" "RRF1" 15 Z9-9
		       "MADR",     // 3800 "B33E" "MADR" "RRF1" 15
		       "MSDR",     // 3810 "B33F" "MSDR" "RRF1" 15
		       "LPXBR",    // 3820 "B340" "LPXBR" "RRE" 14
		       "LNXBR",    // 3830 "B341" "LNXBR" "RRE" 14
		       "LTXBR",    // 3840 "B342" "LTXBR" "RRE" 14
		       "LCXBR",    // 3850 "B343" "LCXBR" "RRE" 14
		       "LEDBR",    // 3860 "B344" "LEDBR" "RRE" 14
		       "LDXBR",    // 3870 "B345" "LDXBR" "RRE" 14
		       "LEXBR",    // 3880 "B346" "LEXBR" "RRE" 14
		       "FIXBR",    // 3890 "B347" "FIXBR" "RRF2" 34
		       "KXBR",     // 3900 "B348" "KXBR" "RRE" 14
		       "CXBR",     // 3910 "B349" "CXBR" "RRE" 14
		       "AXBR",     // 3920 "B34A" "AXBR" "RRE" 14
		       "SXBR",     // 3930 "B34B" "SXBR" "RRE" 14
		       "MXBR",     // 3940 "B34C" "MXBR" "RRE" 14
		       "DXBR",     // 3950 "B34D" "DXBR" "RRE" 14
		       "TBEDR",    // 3960 "B350" "TBEDR" "RRF2" 34
		       "TBDR",     // 3970 "B351" "TBDR" "RRF2" 34
		       "DIEBR",    // 3980 "B353" "DIEBR" "RRF3" 30
		       "FIEBR",    // 3990 "B357" "FIEBR" "RRF2" 34
		       "THDER",    // 4000 "B358" "THDER" "RRE" 14
		       "THDR",     // 4010 "B359" "THDR" "RRE" 14
		       "DIDBR",    // 4020 "B35B" "DIDBR" "RRF3" 30
		       "FIDBR",    // 4030 "B35F" "FIDBR" "RRF2" 34
		       "LPXR",     // 4040 "B360" "LPXR" "RRE" 14
		       "LNXR",     // 4050 "B361" "LNXR" "RRE" 14
		       "LTXR",     // 4060 "B362" "LTXR" "RRE" 14
		       "LCXR",     // 4070 "B363" "LCXR" "RRE" 14
		       "LXR",      // 4080 "B365" "LXR" "RRE" 14
		       "LEXR",     // 4090 "B366" "LEXR" "RRE" 14
		       "FIXR",     // 4100 "B367" "FIXR" "RRE" 14
		       "CXR",      // 4110 "B369" "CXR" "RRE" 14
		       "LZER",     // 4120 "B374" "LZER" "RRE" 14
		       "LZDR",     // 4130 "B375" "LZDR" "RRE" 14
		       "LZXR",     // 4140 "B376" "LZXR" "RRE" 14
		       "FIER",     // 4150 "B377" "FIER" "RRE" 14
		       "FIDR",     // 4160 "B37F" "FIDR" "RRE" 14
		       "SFPC",     // 4170 "B384" "SFPC" "RRE" 14
		       "EFPC",     // 4180 "B38C" "EFPC" "RRE" 14
		       "CEFBR",    // 4190 "B394" "CEFBR" "RRE" 14
		       "CDFBR",    // 4200 "B395" "CDFBR" "RRE" 14
		       "CXFBR",    // 4210 "B396" "CXFBR" "RRE" 14
		       "CFEBR",    // 4220 "B398" "CFEBR" "RRF2" 34
		       "CFDBR",    // 4230 "B399" "CFDBR" "RRF2" 34
		       "CFXBR",    // 4240 "B39A" "CFXBR" "RRF2" 34
		       "CEGBR",    // 4250 "B3A4" "CEGBR" "RRE" 14
		       "CDGBR",    // 4260 "B3A5" "CDGBR" "RRE" 14
		       "CXGBR",    // 4270 "B3A6" "CXGBR" "RRE" 14
		       "CGEBR",    // 4280 "B3A8" "CGEBR" "RRF2" 34
		       "CGDBR",    // 4290 "B3A9" "CGDBR" "RRF2" 34
		       "CGXBR",    // 4300 "B3AA" "CGXBR" "RRF2" 34
		       "CEFR",     // 4310 "B3B4" "CEFR" "RRE" 14
		       "CDFR",     // 4320 "B3B5" "CDFR" "RRE" 14
		       "CXFR",     // 4330 "B3B6" "CXFR" "RRE" 14
		       "CFER",     // 4340 "B3B8" "CFER" "RRF2" 34
		       "CFDR",     // 4350 "B3B9" "CFDR" "RRF2" 34
		       "CFXR",     // 4360 "B3BA" "CFXR" "RRF2" 34
		       "CEGR",     // 4370 "B3C4" "CEGR" "RRE" 14
		       "CDGR",     // 4380 "B3C5" "CDGR" "RRE" 14
		       "CXGR",     // 4390 "B3C6" "CXGR" "RRE" 14
		       "CGER",     // 4400 "B3C8" "CGER" "RRF2" 34
		       "CGDR",     // 4410 "B3C9" "CGDR" "RRF2" 34
		       "CGXR",     // 4420 "B3CA" "CGXR" "RRF2" 34
		       "STCTL",    // 4430 "B6" "STCTL" "RS" 10
		       "LCTL",     // 4440 "B7" "LCTL" "RS" 10
		       "LPGR",     // 4450 "B900" "LPGR" "RRE" 14
		       "LNGR",     // 4460 "B901" "LNGR" "RRE" 14
		       "LTGR",     // 4470 "B902" "LTGR" "RRE" 14
		       "LCGR",     // 4480 "B903" "LCGR" "RRE" 14
		       "LGR",      // 4490 "B904" "LGR" "RRE" 14
		       "LURAG",    // 4500 "B905" "LURAG" "RRE" 14
		       "LGBR",     //      "B906" "LGBR" "RRE" 14 Z9-10
		       "LGHR",     //      "B907" "LGHR" "RRE" 14 Z9-11
		       "AGR",      // 4510 "B908" "AGR" "RRE" 14
		       "SGR",      // 4520 "B909" "SGR" "RRE" 14
		       "ALGR",     // 4530 "B90A" "ALGR" "RRE" 14
		       "SLGR",     // 4540 "B90B" "SLGR" "RRE" 14
		       "MSGR",     // 4550 "B90C" "MSGR" "RRE" 14
		       "DSGR",     // 4560 "B90D" "DSGR" "RRE" 14
		       "EREGG",    // 4570 "B90E" "EREGG" "RRE" 14
		       "LRVGR",    // 4580 "B90F" "LRVGR" "RRE" 14
		       "LPGFR",    // 4590 "B910" "LPGFR" "RRE" 14
		       "LNGFR",    // 4600 "B911" "LNGFR" "RRE" 14
		       "LTGFR",    // 4610 "B912" "LTGFR" "RRE" 14
		       "LCGFR",    // 4620 "B913" "LCGFR" "RRE" 14
		       "LGFR",     // 4630 "B914" "LGFR" "RRE" 14
		       "LLGFR",    // 4640 "B916" "LLGFR" "RRE" 14
		       "LLGTR",    // 4650 "B917" "LLGTR" "RRE" 14
		       "AGFR",     // 4660 "B918" "AGFR" "RRE" 14
		       "SGFR",     // 4670 "B919" "SGFR" "RRE" 14
		       "ALGFR",    // 4680 "B91A" "ALGFR" "RRE" 14
		       "SLGFR",    // 4690 "B91B" "SLGFR" "RRE" 14
		       "MSGFR",    // 4700 "B91C" "MSGFR" "RRE" 14
		       "DSGFR",    // 4710 "B91D" "DSGFR" "RRE" 14
		       "KMAC",     // 4720 "B91E" "KMAC" "RRE" 14
		       "LRVR",     // 4730 "B91F" "LRVR" "RRE" 14
		       "CGR",      // 4740 "B920" "CGR" "RRE" 14
		       "CLGR",     // 4750 "B921" "CLGR" "RRE" 14
		       "STURG",    // 4760 "B925" "STURG" "RRE" 14
		       "LBR",      //      "B926" "LBR" "RRE" 14 Z9-12
		       "LHR",      //      "B927" "LHR" "RRE" 14 Z9-13
		       "KM",       // 4770 "B92E" "KM" "RRE" 14
		       "KMC",      // 4780 "B92F" "KMC" "RRE" 14
		       "CGFR",     // 4790 "B930" "CGFR" "RRE" 14
		       "CLGFR",    // 4800 "B931" "CLGFR" "RRE" 14
		       "KIMD",     // 4810 "B93E" "KIMD" "RRE" 14
		       "KLMD",     // 4820 "B93F" "KLMD" "RRE" 14
		       "BCTGR",    // 4830 "B946" "BCTGR" "RRE" 14
		       "NGR",      // 4840 "B980" "NGR" "RRE" 14
		       "OGR",      // 4850 "B981" "OGR" "RRE" 14
		       "XGR",      // 4860 "B982" "XGR" "RRE" 14
		       "FLOGR",    //      "B983" "FLOGR" "RRE" 14 Z9-14
		       "LLGCR",    //      "B984" "LLGCR" "RRE" 14 Z9-15
		       "LLGHR",    //      "B985" "LLGHR" "RRE" 14 Z9-16
		       "MLGR",     // 4870 "B986" "MLGR" "RRE" 14
		       "DLGR",     // 4880 "B987" "DLGR" "RRE" 14
		       "ALCGR",    // 4890 "B988" "ALCGR" "RRE" 14
		       "SLBGR",    // 4900 "B989" "SLBGR" "RRE" 14
		       "CSPG",     // 4910 "B98A" "CSPG" "RRE" 14
		       "EPSW",     // 4920 "B98D" "EPSW" "RRE" 14
		       "IDTE",     // 4930 "B98E" "IDTE" "RRF2" 34
		       "TRTT",     // 4940 "B990" "TRTT" "RRE" 14
		       "TRTO",     // 4950 "B991" "TRTO" "RRE" 14
		       "TROT",     // 4960 "B992" "TROT" "RRE" 14
		       "TROO",     // 4970 "B993" "TROO" "RRE" 14
		       "LLCR",     //      "B994" "LLCR" "RRE" 14 Z9-17
		       "LLHR",     //      "B995" "LLHR" "RRE" 14 Z9-18
		       "MLR",      // 4980 "B996" "MLR" "RRE" 14
		       "DLR",      // 4990 "B997" "DLR" "RRE" 14
		       "ALCR",     // 5000 "B998" "ALCR" "RRE" 14
		       "SLBR",     // 5010 "B999" "SLBR" "RRE" 14
		       "EPAIR",    // 5020 "B99A" "EPAIR" "RRE" 14
		       "ESAIR",    // 5030 "B99B" "ESAIR" "RRE" 14
		       "ESEA",     // 5040 "B99D" "ESEA" "RRE" 14
		       "PTI",      // 5050 "B99E" "PTI" "RRE" 14
		       "SSAIR",    // 5060 "B99F" "SSAIR" "RRE" 14
		       "LPTEA",    //      "B9AA" "LPTEA" "RRE" 14 Z9-19
		       "CU14",     // 5070 "B9B0" "CU14" "RRE" 14
		       "CU24",     // 5080 "B9B1" "CU24" "RRE" 14
		       "CU41",     // 5090 "B9B2" "CU41" "RRE" 14
		       "CU42",     // 5100 "B9B3" "CU42" "RRE" 14
		       "SRSTU",    // 5110 "B9BE" "SRSTU" "RRE" 14
		       "CS",       // 5120 "BA" "CS" "RS" 10
		       "CDS",      // 5130 "BB" "CDS" "RS" 10
		       "CLM",      // 5140 "BD" "CLM" "RS" 10
		       "STCM",     // 5150 "BE" "STCM" "RS" 10
		       "ICM",      // 5160 "BF" "ICM" "RS" 10
		       "LARL",     // 5170 "C00" "LARL" "RIL" 16
		       "LGFI",     //      "C01" "LGFI" "RIL" 16 Z9-20
		       "BRCL",     // 5180 "C04" "BRCL" "RIL" 16
		       "JLNOP",    // 5390 "C040" "JLNOP" "BLX" 33
		       "BROL",     // 5400 "C041" "BROL" "BLX" 33
		       "JLO",      // 5410 "C041" "JLO" "BLX" 33
		       "BRHL",     // 5420 "C042" "BRHL" "BLX" 33
		       "BRPL",     // 5430 "C042" "BRPL" "BLX" 33
		       "JLH",      // 5440 "C042" "JLH" "BLX" 33
		       "JLP",      // 5450 "C042" "JLP" "BLX" 33
		       "BRLL",     // 5460 "C044" "BRLL" "BLX" 33
		       "BRML",     // 5470 "C044" "BRML" "BLX" 33
		       "JLL",      // 5480 "C044" "JLL" "BLX" 33
		       "JLM",      // 5490 "C044" "JLM" "BLX" 33
		       "BRNEL",    // 5500 "C047" "BRNEL" "BLX" 33
		       "BRNZL",    // 5510 "C047" "BRNZL" "BLX" 33
		       "JLNE",     // 5520 "C047" "JLNE" "BLX" 33
		       "JLNZ",     // 5530 "C047" "JLNZ" "BLX" 33
		       "BREL",     // 5540 "C048" "BREL" "BLX" 33
		       "BRZL",     // 5550 "C048" "BRZL" "BLX" 33
		       "JLE",      // 5560 "C048" "JLE" "BLX" 33
		       "JLZ",      // 5570 "C048" "JLZ" "BLX" 33
		       "BRNLL",    // 5580 "C04B" "BRNLL" "BLX" 33
		       "BRNML",    // 5590 "C04B" "BRNML" "BLX" 33
		       "JLNL",     // 5600 "C04B" "JLNL" "BLX" 33
		       "JLNM",     // 5610 "C04B" "JLNM" "BLX" 33
		       "BRNHL",    // 5620 "C04D" "BRNHL" "BLX" 33
		       "BRNPL",    // 5630 "C04D" "BRNPL" "BLX" 33
		       "JLNH",     // 5640 "C04D" "JLNH" "BLX" 33
		       "JLNP",     // 5650 "C04D" "JLNP" "BLX" 33
		       "BRNOL",    // 5660 "C04E" "BRNOL" "BLX" 33
		       "JLNO",     // 5670 "C04E" "JLNO" "BLX" 33
		       "BRUL",     // 5680 "C04F" "BRUL" "BLX" 33
		       "JLU",      // 5690 "C04F" "JLU" "BLX" 33
		       "BRASL",    // 5210 "C05" "BRASL" "RIL" 16
		       "JASL",     // 5220 "C05" "JASL" "RIL" 16
		       "XIHF",     //      "C06" "XIHF" "RIL" 16 Z9-21
		       "XILF",     //      "C07" "XILF" "RIL" 16 Z9-22
		       "IIHF",     //      "C08" "IIHF" "RIL" 16 Z9-23
		       "IILF",     //      "C09" "IILF" "RIL" 16 Z9-24
		       "NIHF",     //      "C0A" "NIHF" "RIL" 16 Z9-25
		       "NILF",     //      "C0B" "NILF" "RIL" 16 Z9-26
		       "OIHF",     //      "C0C" "OIHF" "RIL" 16 Z9-27
		       "OILF",     //      "C0D" "OILF" "RIL" 16 Z9-28
		       "LLIHF",    //      "C0E" "LLIHF" "RIL" 16 Z9-29
		       "LLILF",    //      "C0F" "LLILF" "RIL" 16 Z9-30
		       "SLGFI",    //      "C24" "SLGFI" "RIL" 16 Z9-31
		       "SLFI",     //      "C25" "SLFI" "RIL" 16 Z9-32
		       "AGFI",     //      "C28" "AGFI" "RIL" 16 Z9-33
		       "AFI",      //      "C29" "AFI" "RIL" 16 Z9-34
		       "ALGFI",    //      "C2A" "ALGFI" "RIL" 16 Z9-35
		       "ALFI",     //      "C2B" "ALFI" "RIL" 16 Z9-36
		       "CGFI",     //      "C2C" "CGFI" "RIL" 16 Z9-37
		       "CFI",      //      "C2D" "CFI" "RIL" 16 Z9-38
		       "CLGFI",    //      "C2E" "CLGFI" "RIL" 16 Z9-39
		       "CLFI",     //      "C2F" "CLFI" "RIL" 16 Z9-40
		       "MVCOS",    //      "C80" "MVCOS" "SSF" 17 Z9-41
		       "TRTR",     // 5230 "D0" "TRTR" "SS" 17
		       "MVN",      // 5240 "D1" "MVN" "SS" 17
		       "MVC",      // 5250 "D2" "MVC" "SS" 17
		       "MVZ",      // 5260 "D3" "MVZ" "SS" 17
		       "NC",       // 5270 "D4" "NC" "SS" 17
		       "CLC",      // 5280 "D5" "CLC" "SS" 17
		       "OC",       // 5290 "D6" "OC" "SS" 17
		       "XC",       // 5300 "D7" "XC" "SS" 17
		       "MVCK",     // 5310 "D9" "MVCK" "SS" 17
		       "MVCP",     // 5320 "DA" "MVCP" "SS" 17
		       "MVCS",     // 5330 "DB" "MVCS" "SS" 17
		       "TR",       // 5340 "DC" "TR" "SS" 17
		       "TRT",      // 5350 "DD" "TRT" "SS" 17
		       "ED",       // 5360 "DE" "ED" "SS" 17
		       "EDMK",     // 5370 "DF" "EDMK" "SS" 17
		       "PKU",      // 5380 "E1" "PKU" "SS" 17
		       "UNPKU",    // 5390 "E2" "UNPKU" "SS" 17
		       "LTG",      //      "E302" "LTG" "RXY" 18 Z9-42
		       "LRAG",     // 5400 "E303" "LRAG" "RXY" 18
		       "LG",       // 5410 "E304" "LG" "RXY" 18
		       "CVBY",     // 5420 "E306" "CVBY" "RXY" 18
		       "AG",       // 5430 "E308" "AG" "RXY" 18
		       "SG",       // 5440 "E309" "SG" "RXY" 18
		       "ALG",      // 5450 "E30A" "ALG" "RXY" 18
		       "SLG",      // 5460 "E30B" "SLG" "RXY" 18
		       "MSG",      // 5470 "E30C" "MSG" "RXY" 18
		       "DSG",      // 5480 "E30D" "DSG" "RXY" 18
		       "CVBG",     // 5490 "E30E" "CVBG" "RXY" 18
		       "LRVG",     // 5500 "E30F" "LRVG" "RXY" 18
		       "LT",       //      "E312" "LT" "RXY" 18 Z9-43
		       "LRAY",     // 5510 "E313" "LRAY" "RXY" 18
		       "LGF",      // 5520 "E314" "LGF" "RXY" 18
		       "LGH",      // 5530 "E315" "LGH" "RXY" 18
		       "LLGF",     // 5540 "E316" "LLGF" "RXY" 18
		       "LLGT",     // 5550 "E317" "LLGT" "RXY" 18
		       "AGF",      // 5560 "E318" "AGF" "RXY" 18
		       "SGF",      // 5570 "E319" "SGF" "RXY" 18
		       "ALGF",     // 5580 "E31A" "ALGF" "RXY" 18
		       "SLGF",     // 5590 "E31B" "SLGF" "RXY" 18
		       "MSGF",     // 5600 "E31C" "MSGF" "RXY" 18
		       "DSGF",     // 5610 "E31D" "DSGF" "RXY" 18
		       "LRV",      // 5620 "E31E" "LRV" "RXY" 18
		       "LRVH",     // 5630 "E31F" "LRVH" "RXY" 18
		       "CG",       // 5640 "E320" "CG" "RXY" 18
		       "CLG",      // 5650 "E321" "CLG" "RXY" 18
		       "STG",      // 5660 "E324" "STG" "RXY" 18
		       "CVDY",     // 5670 "E326" "CVDY" "RXY" 18
		       "CVDG",     // 5680 "E32E" "CVDG" "RXY" 18
		       "STRVG",    // 5690 "E32F" "STRVG" "RXY" 18
		       "CGF",      // 5700 "E330" "CGF" "RXY" 18
		       "CLGF",     // 5710 "E331" "CLGF" "RXY" 18
		       "STRV",     // 5720 "E33E" "STRV" "RXY" 18
		       "STRVH",    // 5730 "E33F" "STRVH" "RXY" 18
		       "BCTG",     // 5740 "E346" "BCTG" "RXY" 18
		       "STY",      // 5750 "E350" "STY" "RXY" 18
		       "MSY",      // 5760 "E351" "MSY" "RXY" 18
		       "NY",       // 5770 "E354" "NY" "RXY" 18
		       "CLY",      // 5780 "E355" "CLY" "RXY" 18
		       "OY",       // 5790 "E356" "OY" "RXY" 18
		       "XY",       // 5800 "E357" "XY" "RXY" 18
		       "LY",       // 5810 "E358" "LY" "RXY" 18
		       "CY",       // 5820 "E359" "CY" "RXY" 18
		       "AY",       // 5830 "E35A" "AY" "RXY" 18
		       "SY",       // 5840 "E35B" "SY" "RXY" 18
		       "ALY",      // 5850 "E35E" "ALY" "RXY" 18
		       "SLY",      // 5860 "E35F" "SLY" "RXY" 18
		       "STHY",     // 5870 "E370" "STHY" "RXY" 18
		       "LAY",      // 5880 "E371" "LAY" "RXY" 18
		       "STCY",     // 5890 "E372" "STCY" "RXY" 18
		       "ICY",      // 5900 "E373" "ICY" "RXY" 18
		       "LB",       // 5910 "E376" "LB" "RXY" 18
		       "LGB",      // 5920 "E377" "LGB" "RXY" 18
		       "LHY",      // 5930 "E378" "LHY" "RXY" 18
		       "CHY",      // 5940 "E379" "CHY" "RXY" 18
		       "AHY",      // 5950 "E37A" "AHY" "RXY" 18
		       "SHY",      // 5960 "E37B" "SHY" "RXY" 18
		       "NG",       // 5970 "E380" "NG" "RXY" 18
		       "OG",       // 5980 "E381" "OG" "RXY" 18
		       "XG",       // 5990 "E382" "XG" "RXY" 18
		       "MLG",      // 6000 "E386" "MLG" "RXY" 18
		       "DLG",      // 6010 "E387" "DLG" "RXY" 18
		       "ALCG",     // 6020 "E388" "ALCG" "RXY" 18
		       "SLBG",     // 6030 "E389" "SLBG" "RXY" 18
		       "STPQ",     // 6040 "E38E" "STPQ" "RXY" 18
		       "LPQ",      // 6050 "E38F" "LPQ" "RXY" 18
		       "LLGC",     // 6060 "E390" "LLGC" "RXY" 18
		       "LLGH",     // 6070 "E391" "LLGH" "RXY" 18
		       "LLC",      //      "E394" "LLC" "RXY" 18 Z9-44
		       "LLH",      //      "E395" "LLH" "RXY" 18 Z9-45
		       "ML",       // 6080 "E396" "ML" "RXY" 18
		       "DL",       // 6090 "E397" "DL" "RXY" 18
		       "ALC",      // 6100 "E398" "ALC" "RXY" 18
		       "SLB",      // 6110 "E399" "SLB" "RXY" 18
		       "LASP",     // 6120 "E500" "LASP" "SSE" 19
		       "TPROT",    // 6130 "E501" "TPROT" "SSE" 19
		       "STRAG",    // 6140 "E502" "STRAG" "SSE" 19
		       "MVCSK",    // 6150 "E50E" "MVCSK" "SSE" 19
		       "MVCDK",    // 6160 "E50F" "MVCDK" "SSE" 19
		       "MVCIN",    // 6170 "E8" "MVCIN" "SS" 17
		       "PKA",      // 6180 "E9" "PKA" "SS" 31
		       "UNPKA",    // 6190 "EA" "UNPKA" "SS" 17
		       "LMG",      // 6200 "EB04" "LMG" "RSY" 20
		       "SRAG",     // 6210 "EB0A" "SRAG" "RSY" 20
		       "SLAG",     // 6220 "EB0B" "SLAG" "RSY" 20
		       "SRLG",     // 6230 "EB0C" "SRLG" "RSY" 20
		       "SLLG",     // 6240 "EB0D" "SLLG" "RSY" 20
		       "TRACG",    // 6250 "EB0F" "TRACG" "RSY" 20
		       "CSY",      // 6260 "EB14" "CSY" "RSY" 20
		       "RLLG",     // 6270 "EB1C" "RLLG" "RSY" 20
		       "RLL",      // 6280 "EB1D" "RLL" "RSY" 20
		       "CLMH",     // 6290 "EB20" "CLMH" "RSY" 20
		       "CLMY",     // 6300 "EB21" "CLMY" "RSY" 20
		       "STMG",     // 6310 "EB24" "STMG" "RSY" 20
		       "STCTG",    // 6320 "EB25" "STCTG" "RSY" 20
		       "STMH",     // 6330 "EB26" "STMH" "RSY" 20
		       "STCMH",    // 6340 "EB2C" "STCMH" "RSY" 20
		       "STCMY",    // 6350 "EB2D" "STCMY" "RSY" 20
		       "LCTLG",    // 6360 "EB2F" "LCTLG" "RSY" 20
		       "CSG",      // 6370 "EB30" "CSG" "RSY" 20
		       "CDSY",     // 6380 "EB31" "CDSY" "RSY" 20
		       "CDSG",     // 6390 "EB3E" "CDSG" "RSY" 20
		       "BXHG",     // 6400 "EB44" "BXHG" "RSY" 20
		       "BXLEG",    // 6410 "EB45" "BXLEG" "RSY" 20
		       "TMY",      // 6420 "EB51" "TMY" "SIY" 21
		       "MVIY",     // 6430 "EB52" "MVIY" "SIY" 21
		       "NIY",      // 6440 "EB54" "NIY" "SIY" 21
		       "CLIY",     // 6450 "EB55" "CLIY" "SIY" 21
		       "OIY",      // 6460 "EB56" "OIY" "SIY" 21
		       "XIY",      // 6470 "EB57" "XIY" "SIY" 21
		       "ICMH",     // 6480 "EB80" "ICMH" "RSY" 20
		       "ICMY",     // 6490 "EB81" "ICMY" "RSY" 20
		       "MVCLU",    // 6500 "EB8E" "MVCLU" "RSY" 20
		       "CLCLU",    // 6510 "EB8F" "CLCLU" "RSY" 20
		       "STMY",     // 6520 "EB90" "STMY" "RSY" 20
		       "LMH",      // 6530 "EB96" "LMH" "RSY" 20
		       "LMY",      // 6540 "EB98" "LMY" "RSY" 20
		       "LAMY",     // 6550 "EB9A" "LAMY" "RSY" 20
		       "STAMY",    // 6560 "EB9B" "STAMY" "RSY" 20
		       "TP",       // 6570 "EBC0" "TP" "RSL" 22
		       "BRXHG",    // 6580 "EC44" "BRXHG" "RIE" 23
		       "JXHG",     // 6590 "EC44" "JXHG" "RIE" 23
		       "BRXLG",    // 6600 "EC45" "BRXLG" "RIE" 23
		       "JXLEG",    // 6610 "EC45" "JXLEG" "RIE" 23
		       "LDEB",     // 6620 "ED04" "LDEB" "RXE" 24
		       "LXDB",     // 6630 "ED05" "LXDB" "RXE" 24
		       "LXEB",     // 6640 "ED06" "LXEB" "RXE" 24
		       "MXDB",     // 6650 "ED07" "MXDB" "RXE" 24
		       "KEB",      // 6660 "ED08" "KEB" "RXE" 24
		       "CEB",      // 6670 "ED09" "CEB" "RXE" 24
		       "AEB",      // 6680 "ED0A" "AEB" "RXE" 24
		       "SEB",      // 6690 "ED0B" "SEB" "RXE" 24
		       "MDEB",     // 6700 "ED0C" "MDEB" "RXE" 24
		       "DEB",      // 6710 "ED0D" "DEB" "RXE" 24
		       "MAEB",     // 6720 "ED0E" "MAEB" "RXF" 25
		       "MSEB",     // 6730 "ED0F" "MSEB" "RXF" 25
		       "TCEB",     // 6740 "ED10" "TCEB" "RXE" 24
		       "TCDB",     // 6750 "ED11" "TCDB" "RXE" 24
		       "TCXB",     // 6760 "ED12" "TCXB" "RXE" 24
		       "SQEB",     // 6770 "ED14" "SQEB" "RXE" 24
		       "SQDB",     // 6780 "ED15" "SQDB" "RXE" 24
		       "MEEB",     // 6790 "ED17" "MEEB" "RXE" 24
		       "KDB",      // 6800 "ED18" "KDB" "RXE" 24
		       "CDB",      // 6810 "ED19" "CDB" "RXE" 24
		       "ADB",      // 6820 "ED1A" "ADB" "RXE" 24
		       "SDB",      // 6830 "ED1B" "SDB" "RXE" 24
		       "MDB",      // 6840 "ED1C" "MDB" "RXE" 24
		       "DDB",      // 6850 "ED1D" "DDB" "RXE" 24
		       "MADB",     // 6860 "ED1E" "MADB" "RXF" 25
		       "MSDB",     // 6870 "ED1F" "MSDB" "RXF" 25
		       "LDE",      // 6880 "ED24" "LDE" "RXE" 24
		       "LXD",      // 6890 "ED25" "LXD" "RXE" 24
		       "LXE",      // 6900 "ED26" "LXE" "RXE" 24
		       "MAE",      // 6910 "ED2E" "MAE" "RXF" 25
		       "MSE",      // 6920 "ED2F" "MSE" "RXF" 25
		       "SQE",      // 6930 "ED34" "SQE" "RXE" 24
		       "SQD",      // 6940 "ED35" "SQD" "RXE" 24
		       "MEE",      // 6950 "ED37" "MEE" "RXE" 24
		       "MAYL",     //      "ED38" "MAYL" "RXF" 25 Z9-46
		       "MYL",      //      "ED39" "MYL" "RXF" 25 Z9-47
		       "MAY",      //      "ED3A" "MAY" "RXF" 25 Z9-48
		       "MY",       //      "EDEB" "MY" "RXF" 25 Z9-49
		       "MAYH",     //      "ED3C" "MAYH" "RXF" 25 Z9-50
		       "MYH",      //      "EDED" "MYH" "RXF" 25 Z9-51
		       "MAD",      // 6960 "ED3E" "MAD" "RXF" 25
		       "MSD",      // 6970 "ED3F" "MSD" "RXF" 25
		       "LEY",      // 6980 "ED64" "LEY" "RXY" 18
		       "LDY",      // 6990 "ED65" "LDY" "RXY" 18
		       "STEY",     // 7000 "ED66" "STEY" "RXY" 18
		       "STDY",     // 7010 "ED67" "STDY" "RXY" 18
		       "PLO",      // 7020 "EE" "PLO" "SS3" 27
		       "LMD",      // 7030 "EF" "LMD" "SS4" 28
		       "SRP",      // 7040 "F0" "SRP" "SS5" 29
		       "MVO",      // 7050 "F1" "MVO" "SS2" 26
		       "PACK",     // 7060 "F2" "PACK" "SS2" 26
		       "UNPK",     // 7070 "F3" "UNPK" "SS2" 26
		       "ZAP",      // 7080 "F8" "ZAP" "SS2" 26
		       "CP",       // 7090 "F9" "CP" "SS2" 26
		       "AP",       // 7100 "FA" "AP" "SS2" 26
		       "SP",       // 7110 "FB" "SP" "SS2" 26
		       "MP",       // 7120 "FC" "MP" "SS2" 26
		       "DP",      // 7130 "FD" "DP" "SS2" 26
		       "CCW",      // 7140  "CCW"  101
		       "CCW0",     // 7150  "CCW0"  102
		       "CCW1",     // 7160  "CCW1"  103
		       "DC",       // 7170  "DC"  104
		       "DS",       // 7180  "DS"  105
		       "ALIAS",    // 7190  "ALIAS"  106
		       "AMODE",    // 7200  "AMODE"  107
		       "CATTR",    // 7210  "CATTR"  108
		       "COM",      // 7220  "COM"  109
		       "CSECT",    // 7230  "CSECT"  110
		       "CXD",      // 7240  "CXD"  111
		       "DSECT",    // 7250  "DSECT"  112
		       "DXD",      // 7260  "DXD"  113
		       "ENTRY",    // 7270  "ENTRY"  114
		       "EXTRN",    // 7280  "EXTRN"  115
		       "LOCTR",    // 7290  "LOCTR"  116
		       "RMODE",    // 7300  "RMODE"  117
		       "RSECT",    // 7310  "RSECT"  118
		       "START",    // 7320  "START"  119
		       "WXTRN",    // 7330  "WXTRN"  120
		       "XATTR",    // 7340  "XATTR"  121
		       "",         // 7350  ""  122
		       "DROP",     // 7360  "DROP"  123
		       "USING",    // 7370  "USING"  124
		       "AEJECT",   // 7380  "AEJECT"  125
		       "ASPACE",   // 7390  "ASPACE"  126
		       "CEJECT",   // 7400  "CEJECT"  127
		       "EJECT",    // 7410  "EJECT"  128
		       "PRINT",    // 7420  "PRINT"  129
		       "SPACE",    // 7430  "SPACE"  130
		       "TITLE",    // 7440  "TITLE"  131
		       "ADATA",    // 7450  "ADATA"  132
		       "CNOP",     // 7460  "CNOP"  133
		       "COPY",     // 7470  "COPY"  134
		       "END",      // 7480  "END"  135
		       "EQU",      // 7490  "EQU"  136
		       "EXITCTL",  // 7500  "EXITCTL"  137
		       "ICTL",     // 7510  "ICTL"  138
		       "ISEQ",     // 7520  "ISEQ"  139
		       "LTORG",    // 7530  "LTORG"  140
		       "OPSYN",    // 7540  "OPSYN"  141
		       "ORG",      // 7550  "ORG"  142
		       "POP",      // 7560  "POP"  143
		       "PUNCH",    // 7570  "PUNCH"  144
		       "PUSH",     // 7580  "PUSH"  145
		       "REPRO",    // 7590  "REPRO"  146
		       "ACTR",     // 7600  "ACTR"  147
		       "AGO",      // 7610  "AGO"  148
		       "AIF",      // 7620  "AIF"  149
		       "AINSERT",  // 7630  "AINSERT"  150
		       "ANOP",     // 7640  "ANOP"  151
		       "AREAD",    // 7650  "AREAD"  152
		       "GBLA",     // 7660  "GBLA"  153
		       "GBLB",     // 7670  "GBLB"  154
		       "GBLC",     // 7680  "GBLC"  155
		       "LCLA",     // 7690  "LCLA"  156
		       "LCLB",     // 7700  "LCLB"  157
		       "LCLC",     // 7710  "LCLC"  158
		       "MHELP",    // 7720  "MHELP"  159
		       "MNOTE",    // 7730  "MNOTE"  160
		       "SETA",     // 7740  "SETA"  161
		       "SETAF",    // 7750  "SETAF"  162
		       "SETB",     // 7760  "SETB"  163
		       "SETC",     // 7770  "SETC"  164
		       "SETCF",    // 7780  "SETCF"  165
		       "MACRO",    // 7790  "MACRO"  166
		       "MEND",     // 7800  "MEND"  167
		       "MEXIT"    // 7810  "MEXIT"  168
	       
      };
    int[]    op_type_len = {
    	 0, // 0 comment place holder	
         2,	// 1 "E" 8 PR oooo
         2, // 2 "RR" 60  LR  oorr  
         2, // 3 "BRX" 16  BER oomr
         2, // 4 "I" 1 SVC 00ii
         4, // 5 "RX" 52  L  oorxbddd
         4, // 6 "BCX" 16 BE  oomxbddd
         4, // 7 "S" 43 SPM oo00bddd
         4, // 8 "DM" 1  DIAGNOSE 83000000
         4, // 9 "RSI" 4 BRXH  oorriiii
         4, //10 "RS" 25  oorrbddd
         4, //11 "SI" 9 CLI  ooiibddd
         4, //12 "RI" 37 IIHH  ooroiiii
         4, //13 "BRC" 31 BRE  oomoiiii
         4, //14 "RRE" 185  MSR oooo00rr
         4, //15 "RRF1" 28 MAER oooor0rr (r1,r3,r2 maps to r1,r3,r2)
         6, //16 "RIL" 6  BRCL  oomollllllll
         6, //17 "SS" 32  MVC oollbdddbddd  
         6, //18 "RXY" 76 MLG oorxbdddhhoo
         6, //19 "SSE" 5  LASP  oooobdddbddd
         6, //20 "RSY" 31  LMG  oorrbdddhhoo
         6, //21 "SIY" 6  TMY  ooiibdddhhoo
         6, //22 "RSL" 1  TP  oor0bddd00oo
         6, //23 "RIE" 4  BRXLG  oorriiii00oo
         6, //24 "RXE" 28  ADB oorxbddd00oo
         6, //25 "RXF" 8   MAE  oorxbdddr0oo (note r3 before r1)
         6, //26 AP SS2  oollbdddbddd
         6, //27 PLO SS3  oorrbdddbddd  r1,s2,r3,s4
         6, //28 LMD SS5  oorrbdddbddd  r1,r3,s2,s4
         6, //29 SRP SS2  oolibdddbddd s1(l1),s2,i3
         6, //30 "RRF3" 30 DIEBR oooormrr (r1,r3,r2,m4 maps to r3,m4,r1,r2) 
         6, //31 "SS" PKA oollbdddbddd  ll from S2 
         6, //32 "SSF" MVCOS oor0bdddbddd (s1,s2,r3) z9-41
         6, //33 "BLX" BRCL  oomollllllll (label)
         6  //34 "RRF2" FIXBR oooom0rr (r1,m3,r2 maps to m3,r1,r2)
         };
	int    max_op_type_offset = 34; // see changes required
	//  When adding new opcode case:
	//  1.  Increase the above max.
	//  2.  Change above op_type_len table which must match
    //  3.  Change tz390.init_tables to verify max type
    //  4.  Change az390 instruction format cases and
    //  5.  Change pz390 op_type_offset and op_type_mask
  	int[]    op_type  = {
			   0,  // comments
		       1,  // 10 "0101" "PR" "E" 1
		       1,  // 20 "0102" "UPT" "E" 1		       
		       1,  //    "0104" "PTFF" "E" 1 Z9-1
		       1,  // 30 "0107" "SCKPF" "E" 1
		       1,  // 40 "010B" "TAM" "E" 1
		       1,  // 50 "010C" "SAM24" "E" 1
		       1,  // 60 "010D" "SAM31" "E" 1
		       1,  // 70 "010E" "SAM64" "E" 1
		       1,  // 80 "01FF" "TRAP2" "E" 1
		       2,  // 90 "04" "SPM" "RR" 2
		       2,  // 100 "05" "BALR" "RR" 2
		       2,  // 110 "06" "BCTR" "RR" 2
		       2,  // 120 "07" "BCR" "RR" 2
		       3,  // 130 "07F" "BR" "BRX" 3
		       3,  // 140 "070" "NOPR" "BRX" 3
		       3,  // 150 "072" "BHR" "BRX" 3
		       3,  // 160 "074" "BLR" "BRX" 3
		       3,  // 170 "078" "BER" "BRX" 3
		       3,  // 180 "07D" "BNHR" "BRX" 3
		       3,  // 190 "07B" "BNLR" "BRX" 3
		       3,  // 200 "077" "BNER" "BRX" 3
		       3,  // 210 "072" "BPR" "BRX" 3
		       3,  // 220 "071" "BOR" "BRX" 3
		       3,  // 230 "074" "BMR" "BRX" 3
		       3,  // 240 "078" "BZR" "BRX" 3
		       3,  // 250 "07D" "BNPR" "BRX" 3
		       3,  // 260 "07B" "BNMR" "BRX" 3
		       3,  // 270 "077" "BNZR" "BRX" 3
		       3,  // 280 "07E" "BNOR" "BRX" 3
		       4,  // 290 "0A" "SVC" "I" 4
		       2,  // 300 "0B" "BSM" "RR" 2
		       2,  // 310 "0C" "BASSM" "RR" 2
		       2,  // 320 "0D" "BASR" "RR" 2
		       2,  // 330 "0E" "MVCL" "RR" 2
		       2,  // 340 "0F" "CLCL" "RR" 2
		       2,  // 350 "10" "LPR" "RR" 2
		       2,  // 360 "11" "LNR" "RR" 2
		       2,  // 370 "12" "LTR" "RR" 2
		       2,  // 380 "13" "LCR" "RR" 2
		       2,  // 390 "14" "NR" "RR" 2
		       2,  // 400 "15" "CLR" "RR" 2
		       2,  // 410 "16" "OR" "RR" 2
		       2,  // 420 "17" "XR" "RR" 2
		       2,  // 430 "18" "LR" "RR" 2
		       2,  // 440 "19" "CR" "RR" 2
		       2,  // 450 "1A" "AR" "RR" 2
		       2,  // 460 "1B" "SR" "RR" 2
		       2,  // 470 "1C" "MR" "RR" 2
		       2,  // 480 "1D" "DR" "RR" 2
		       2,  // 490 "1E" "ALR" "RR" 2
		       2,  // 500 "1F" "SLR" "RR" 2
		       2,  // 510 "20" "LPDR" "RR" 2
		       2,  // 520 "21" "LNDR" "RR" 2
		       2,  // 530 "22" "LTDR" "RR" 2
		       2,  // 540 "23" "LCDR" "RR" 2
		       2,  // 550 "24" "HDR" "RR" 2
		       2,  // 560 "25" "LDXR" "RR" 2
		       2,  // 570 "25" "LRDR" "RR" 2
		       2,  // 580 "26" "MXR" "RR" 2
		       2,  // 590 "27" "MXDR" "RR" 2
		       2,  // 600 "28" "LDR" "RR" 2
		       2,  // 610 "29" "CDR" "RR" 2
		       2,  // 620 "2A" "ADR" "RR" 2
		       2,  // 630 "2B" "SDR" "RR" 2
		       2,  // 640 "2C" "MDR" "RR" 2
		       2,  // 650 "2D" "DDR" "RR" 2
		       2,  // 660 "2E" "AWR" "RR" 2
		       2,  // 670 "2F" "SWR" "RR" 2
		       2,  // 680 "30" "LPER" "RR" 2
		       2,  // 690 "31" "LNER" "RR" 2
		       2,  // 700 "32" "LTER" "RR" 2
		       2,  // 710 "33" "LCER" "RR" 2
		       2,  // 720 "34" "HER" "RR" 2
		       2,  // 730 "35" "LEDR" "RR" 2
		       2,  // 740 "35" "LRER" "RR" 2
		       2,  // 750 "36" "AXR" "RR" 2
		       2,  // 760 "37" "SXR" "RR" 2
		       2,  // 770 "38" "LER" "RR" 2
		       2,  // 780 "39" "CER" "RR" 2
		       2,  // 790 "3A" "AER" "RR" 2
		       2,  // 800 "3B" "SER" "RR" 2
		       2,  // 810 "3C" "MDER" "RR" 2
		       2,  // 820 "3C" "MER" "RR" 2
		       2,  // 830 "3D" "DER" "RR" 2
		       2,  // 840 "3E" "AUR" "RR" 2
		       2,  // 850 "3F" "SUR" "RR" 2
		       5,  // 860 "40" "STH" "RX" 5
		       5,  // 870 "41" "LA" "RX" 5
		       5,  // 880 "42" "STC" "RX" 5
		       5,  // 890 "43" "IC" "RX" 5
		       5,  // 900 "44" "EX" "RX" 5
		       5,  // 910 "45" "BAL" "RX" 5
		       5,  // 920 "46" "BCT" "RX" 5
		       5,  // 930 "47" "BC" "RX" 5
		       6,  // 940 "47F" "B" "BCX" 6
		       6,  // 950 "470" "NOP" "BCX" 6
		       6,  // 960 "472" "BH" "BCX" 6
		       6,  // 970 "474" "BL" "BCX" 6
		       6,  // 980 "478" "BE" "BCX" 6
		       6,  // 990 "47D" "BNH" "BCX" 6
		       6,  // 1000 "47B" "BNL" "BCX" 6
		       6,  // 1010 "477" "BNE" "BCX" 6
		       6,  // 1020 "472" "BP" "BCX" 6
		       6,  // 1030 "471" "BO" "BCX" 6
		       6,  // 1040 "474" "BM" "BCX" 6
		       6,  // 1050 "478" "BZ" "BCX" 6
		       6,  // 1060 "47D" "BNP" "BCX" 6
		       6,  // 1070 "47B" "BNM" "BCX" 6
		       6,  // 1080 "477" "BNZ" "BCX" 6
		       6,  // 1090 "47E" "BNO" "BCX" 6
		       5,  // 1100 "48" "LH" "RX" 5
		       5,  // 1110 "49" "CH" "RX" 5
		       5,  // 1120 "4A" "AH" "RX" 5
		       5,  // 1130 "4B" "SH" "RX" 5
		       5,  // 1140 "4C" "MH" "RX" 5
		       5,  // 1150 "4D" "BAS" "RX" 5
		       5,  // 1160 "4E" "CVD" "RX" 5
		       5,  // 1170 "4F" "CVB" "RX" 5
		       5,  // 1180 "50" "ST" "RX" 5
		       5,  // 1190 "51" "LAE" "RX" 5
		       5,  // 1200 "54" "N" "RX" 5
		       5,  // 1210 "55" "CL" "RX" 5
		       5,  // 1220 "56" "O" "RX" 5
		       5,  // 1230 "57" "X" "RX" 5
		       5,  // 1240 "58" "L" "RX" 5
		       5,  // 1250 "59" "C" "RX" 5
		       5,  // 1260 "5A" "A" "RX" 5
		       5,  // 1270 "5B" "S" "RX" 5
		       5,  // 1280 "5C" "M" "RX" 5
		       5,  // 1290 "5D" "D" "RX" 5
		       5,  // 1300 "5E" "AL" "RX" 5
		       5,  // 1310 "5F" "SL" "RX" 5
		       5,  // 1320 "60" "STD" "RX" 5
		       5,  // 1330 "67" "MXD" "RX" 5
		       5,  // 1340 "68" "LD" "RX" 5
		       5,  // 1350 "69" "CD" "RX" 5
		       5,  // 1360 "6A" "AD" "RX" 5
		       5,  // 1370 "6B" "SD" "RX" 5
		       5,  // 1380 "6C" "MD" "RX" 5
		       5,  // 1390 "6D" "DD" "RX" 5
		       5,  // 1400 "6E" "AW" "RX" 5
		       5,  // 1410 "6F" "SW" "RX" 5
		       5,  // 1420 "70" "STE" "RX" 5
		       5,  // 1430 "71" "MS" "RX" 5
		       5,  // 1440 "78" "LE" "RX" 5
		       5,  // 1450 "79" "CE" "RX" 5
		       5,  // 1460 "7A" "AE" "RX" 5
		       5,  // 1470 "7B" "SE" "RX" 5
		       5,  // 1480 "7C" "MDE" "RX" 5
		       5,  // 1490 "7C" "ME" "RX" 5
		       5,  // 1500 "7D" "DE" "RX" 5
		       5,  // 1510 "7E" "AU" "RX" 5
		       5,  // 1520 "7F" "SU" "RX" 5
		       7,  // 1530 "8000" "SSM" "S" 7
		       7,  // 1540 "8202" "LPSW" "S" 7
		       8,  // 1550 "83" "DIAGNOSE" "DM" 8
		       9,  // 1560 "84" "BRXH" "RSI" 9
		       9,  // 1570 "84" "JXH" "RSI" 9
		       9,  // 1580 "85" "BRXLE" "RSI" 9
		       9,  // 1590 "85" "JXLE" "RSI" 9
		       10,  // 1600 "86" "BXH" "RS" 10
		       10,  // 1610 "87" "BXLE" "RS" 10
		       10,  // 1620 "88" "SRL" "RS" 10
		       10,  // 1630 "89" "SLL" "RS" 10
		       10,  // 1640 "8A" "SRA" "RS" 10
		       10,  // 1650 "8B" "SLA" "RS" 10
		       10,  // 1660 "8C" "SRDL" "RS" 10
		       10,  // 1670 "8D" "SLDL" "RS" 10
		       10,  // 1680 "8E" "SRDA" "RS" 10
		       10,  // 1690 "8F" "SLDA" "RS" 10
		       10,  // 1700 "90" "STM" "RS" 10
		       11,  // 1710 "91" "TM" "SI" 11
		       11,  // 1720 "92" "MVI" "SI" 11
		       7,  // 1730 "93" "TS" "S" 7
		       11,  // 1740 "94" "NI" "SI" 11
		       11,  // 1750 "95" "CLI" "SI" 11
		       11,  // 1760 "96" "OI" "SI" 11
		       11,  // 1770 "97" "XI" "SI" 11
		       10,  // 1780 "98" "LM" "RS" 10
		       10,  // 1790 "99" "TRACE" "RS" 10
		       10,  // 1800 "9A" "LAM" "RS" 10
		       10,  // 1810 "9B" "STAM" "RS" 10
		       12,  // 1820 "A50" "IIHH" "RI" 12
		       12,  // 1830 "A51" "IIHL" "RI" 12
		       12,  // 1840 "A52" "IILH" "RI" 12
		       12,  // 1850 "A53" "IILL" "RI" 12
		       12,  // 1860 "A54" "NIHH" "RI" 12
		       12,  // 1870 "A55" "NIHL" "RI" 12
		       12,  // 1880 "A56" "NILH" "RI" 12
		       12,  // 1890 "A57" "NILL" "RI" 12
		       12,  // 1900 "A58" "OIHH" "RI" 12
		       12,  // 1910 "A59" "OIHL" "RI" 12
		       12,  // 1920 "A5A" "OILH" "RI" 12
		       12,  // 1930 "A5B" "OILL" "RI" 12
		       12,  // 1940 "A5C" "LLIHH" "RI" 12
		       12,  // 1950 "A5D" "LLIHL" "RI" 12
		       12,  // 1960 "A5E" "LLILH" "RI" 12
		       12,  // 1970 "A5F" "LLILL" "RI" 12
		       12,  // 1980 "A70" "TMLH" "RI" 12
		       12,  // 1990 "A70" "TMH" "RI" 12
		       12,  // 2000 "A71" "TMLL" "RI" 12
		       12,  // 2010 "A71" "TML" "RI" 12
		       12,  // 2020 "A72" "TMHH" "RI" 12
		       12,  // 2030 "A73" "TMHL" "RI" 12
		       12,  // 2040 "A74" "BRC" "RI" 12
		       13,  // 2050 "A74F" "J" "BRC" 13
		       13,  // 2060 "A740" "JNOP" "BRC" 13
		       13,  // 2070 "A74F" "BRU" "BRC" 13
		       13,  // 2080 "A742" "BRH" "BRC" 13
		       13,  // 2090 "A744" "BRL" "BRC" 13
		       13,  // 2100 "A748" "BRE" "BRC" 13
		       13,  // 2110 "A74D" "BRNH" "BRC" 13
		       13,  // 2120 "A74B" "BRNL" "BRC" 13
		       13,  // 2130 "A747" "BRNE" "BRC" 13
		       13,  // 2140 "A742" "BRP" "BRC" 13
		       13,  // 2150 "A744" "BRM" "BRC" 13
		       13,  // 2160 "A748" "BRZ" "BRC" 13
		       13,  // 2170 "A741" "BRO" "BRC" 13
		       13,  // 2180 "A74D" "BRNP" "BRC" 13
		       13,  // 2190 "A74B" "BRNM" "BRC" 13
		       13,  // 2200 "A747" "BRNZ" "BRC" 13
		       13,  // 2210 "A74E" "BRNO" "BRC" 13
		       13,  // 2220 "A742" "JH" "BRC" 13
		       13,  // 2230 "A744" "JL" "BRC" 13
		       13,  // 2240 "A748" "JE" "BRC" 13
		       13,  // 2250 "A74D" "JNH" "BRC" 13
		       13,  // 2260 "A74B" "JNL" "BRC" 13
		       13,  // 2270 "A747" "JNE" "BRC" 13
		       13,  // 2280 "A742" "JP" "BRC" 13
		       13,  // 2290 "A744" "JM" "BRC" 13
		       13,  // 2300 "A748" "JZ" "BRC" 13
		       13,  // 2310 "A741" "JO" "BRC" 13
		       13,  // 2320 "A74D" "JNP" "BRC" 13
		       13,  // 2330 "A74B" "JNM" "BRC" 13
		       13,  // 2340 "A747" "JNZ" "BRC" 13
		       13,  // 2350 "A74E" "JNO" "BRC" 13
		       12,  // 2360 "A75" "BRAS" "RI" 12
		       12,  // 2370 "A75" "JAS" "RI" 12
		       12,  // 2380 "A76" "BRCT" "RI" 12
		       12,  // 2390 "A76" "JCT" "RI" 12
		       12,  // 2400 "A77" "BRCTG" "RI" 12
		       12,  // 2410 "A77" "JCTG" "RI" 12
		       12,  // 2420 "A78" "LHI" "RI" 12
		       12,  // 2430 "A79" "LGHI" "RI" 12
		       12,  // 2440 "A7A" "AHI" "RI" 12
		       12,  // 2450 "A7B" "AGHI" "RI" 12
		       12,  // 2460 "A7C" "MHI" "RI" 12
		       12,  // 2470 "A7D" "MGHI" "RI" 12
		       12,  // 2480 "A7E" "CHI" "RI" 12
		       12,  // 2490 "A7F" "CGHI" "RI" 12
		       10,  // 2500 "A8" "MVCLE" "RS" 10
		       10,  // 2510 "A9" "CLCLE" "RS" 10
		       11,  // 2520 "AC" "STNSM" "SI" 11
		       11,  // 2530 "AD" "STOSM" "SI" 11
		       10,  // 2540 "AE" "SIGP" "RS" 10
		       11,  // 2550 "AF" "MC" "SI" 11
		       5,  // 2560 "B1" "LRA" "RX" 5
		       7,  // 2570 "B202" "STIDP" "S" 7
		       7,  // 2580 "B204" "SCK" "S" 7
		       7,  // 2590 "B205" "STCK" "S" 7
		       7,  // 2600 "B206" "SCKC" "S" 7
		       7,  // 2610 "B207" "STCKC" "S" 7
		       7,  // 2620 "B208" "SPT" "S" 7
		       7,  // 2630 "B209" "STPT" "S" 7
		       7,  // 2640 "B20A" "SPKA" "S" 7
		       7,  // 2650 "B20B" "IPK" "S" 7
		       7,  // 2660 "B20D" "PTLB" "S" 7
		       7,  // 2670 "B210" "SPX" "S" 7
		       7,  // 2680 "B211" "STPX" "S" 7
		       7,  // 2690 "B212" "STAP" "S" 7
		       7,  // 2700 "B218" "PC" "S" 7
		       7,  // 2710 "B219" "SAC" "S" 7
		       7,  // 2720 "B21A" "CFC" "S" 7
		       14,  // 2730 "B221" "IPTE" "RRE" 14
		       14,  // 2740 "B222" "IPM" "RRE" 14
		       14,  // 2750 "B223" "IVSK" "RRE" 14
		       14,  // 2760 "B224" "IAC" "RRE" 14
		       14,  // 2770 "B225" "SSAR" "RRE" 14
		       14,  // 2780 "B226" "EPAR" "RRE" 14
		       14,  // 2790 "B227" "ESAR" "RRE" 14
		       14,  // 2800 "B228" "PT" "RRE" 14
		       14,  // 2810 "B229" "ISKE" "RRE" 14
		       14,  // 2820 "B22A" "RRBE" "RRE" 14
		       14,  // 2830 "B22B" "SSKE" "RRE" 14
		       14,  // 2840 "B22C" "TB" "RRE" 14
		       14,  // 2850 "B22D" "DXR" "RRE" 14
		       14,  // 2860 "B22E" "PGIN" "RRE" 14
		       14,  // 2870 "B22F" "PGOUT" "RRE" 14
		       7,  // 2880 "B230" "CSCH" "S" 7
		       7,  // 2890 "B231" "HSCH" "S" 7
		       7,  // 2900 "B232" "MSCH" "S" 7
		       7,  // 2910 "B233" "SSCH" "S" 7
		       7,  // 2920 "B234" "STSCH" "S" 7
		       7,  // 2930 "B235" "TSCH" "S" 7
		       7,  // 2940 "B236" "TPI" "S" 7
		       7,  // 2950 "B237" "SAL" "S" 7
		       7,  // 2960 "B238" "RSCH" "S" 7
		       7,  // 2970 "B239" "STCRW" "S" 7
		       7,  // 2980 "B23A" "STCPS" "S" 7
		       7,  // 2990 "B23B" "RCHP" "S" 7
		       7,  // 3000 "B23C" "SCHM" "S" 7
		       14,  // 3010 "B240" "BAKR" "RRE" 14
		       14,  // 3020 "B241" "CKSM" "RRE" 14
		       14,  // 3030 "B244" "SQDR" "RRE" 14
		       14,  // 3040 "B245" "SQER" "RRE" 14
		       14,  // 3050 "B246" "STURA" "RRE" 14
		       14,  // 3060 "B247" "MSTA" "RRE" 14
		       14,  // 3070 "B248" "PALB" "RRE" 14
		       14,  // 3080 "B249" "EREG" "RRE" 14
		       14,  // 3090 "B24A" "ESTA" "RRE" 14
		       14,  // 3100 "B24B" "LURA" "RRE" 14
		       14,  // 3110 "B24C" "TAR" "RRE" 14
		       14,  // 3120 "B24D" "CPYA" "RRE" 14
		       14,  // 3130 "B24E" "SAR" "RRE" 14
		       14,  // 3140 "B24F" "EAR" "RRE" 14
		       14,  // 3150 "B250" "CSP" "RRE" 14
		       14,  // 3160 "B252" "MSR" "RRE" 14
		       14,  // 3170 "B254" "MVPG" "RRE" 14
		       14,  // 3180 "B255" "MVST" "RRE" 14
		       14,  // 3190 "B257" "CUSE" "RRE" 14
		       14,  // 3200 "B258" "BSG" "RRE" 14
		       14,  // 3210 "B25A" "BSA" "RRE" 14
		       14,  // 3220 "B25D" "CLST" "RRE" 14
		       14,  // 3230 "B25E" "SRST" "RRE" 14
		       14,  // 3240 "B263" "CMPSC" "RRE" 14
		       7,  // 3250 "B276" "XSCH" "S" 7
		       7,  // 3260 "B277" "RP" "S" 7
		       7,  // 3270 "B278" "STCKE" "S" 7
		       7,  // 3280 "B279" "SACF" "S" 7
		       7,  //      "B27C" "STCKF" "S" 7 Z9-2
		       7,  // 3290 "B27D" "STSI" "S" 7
		       7,  // 3300 "B299" "SRNM" "S" 7
		       7,  // 3310 "B29C" "STFPC" "S" 7
		       7,  // 3320 "B29D" "LFPC" "S" 7
		       14,  // 3330 "B2A5" "TRE" "RRE" 14
		       14,  // 3340 "B2A6" "CUUTF" "RRE" 14
		       14,  // 3350 "B2A6" "CU21" "RRE" 14
		       14,  // 3360 "B2A7" "CUTFU" "RRE" 14
		       14,  // 3370 "B2A7" "CU12" "RRE" 14
		       7,  //      "B2B0" "STFLE" "S" 7 Z9-3
		       7,  // 3380 "B2B1" "STFL" "S" 7
		       7,  // 3390 "B2B2" "LPSWE" "S" 7
		       7,  // 3400 "B2FF" "TRAP4" "S" 7
		       14,  // 3410 "B300" "LPEBR" "RRE" 14
		       14,  // 3420 "B301" "LNEBR" "RRE" 14
		       14,  // 3430 "B302" "LTEBR" "RRE" 14
		       14,  // 3440 "B303" "LCEBR" "RRE" 14
		       14,  // 3450 "B304" "LDEBR" "RRE" 14
		       14,  // 3460 "B305" "LXDBR" "RRE" 14
		       14,  // 3470 "B306" "LXEBR" "RRE" 14
		       14,  // 3480 "B307" "MXDBR" "RRE" 14
		       14,  // 3490 "B308" "KEBR" "RRE" 14
		       14,  // 3500 "B309" "CEBR" "RRE" 14
		       14,  // 3510 "B30A" "AEBR" "RRE" 14
		       14,  // 3520 "B30B" "SEBR" "RRE" 14
		       14,  // 3530 "B30C" "MDEBR" "RRE" 14
		       14,  // 3540 "B30D" "DEBR" "RRE" 14
		       15,  // 3550 "B30E" "MAEBR" "RRF1" 15
		       15,  // 3560 "B30F" "MSEBR" "RRF1" 15
		       14,  // 3570 "B310" "LPDBR" "RRE" 14
		       14,  // 3580 "B311" "LNDBR" "RRE" 14
		       14,  // 3590 "B312" "LTDBR" "RRE" 14
		       14,  // 3600 "B313" "LCDBR" "RRE" 14
		       14,  // 3610 "B314" "SQEBR" "RRE" 14
		       14,  // 3620 "B315" "SQDBR" "RRE" 14
		       14,  // 3630 "B316" "SQXBR" "RRE" 14
		       14,  // 3640 "B317" "MEEBR" "RRE" 14
		       14,  // 3650 "B318" "KDBR" "RRE" 14
		       14,  // 3660 "B319" "CDBR" "RRE" 14
		       14,  // 3670 "B31A" "ADBR" "RRE" 14
		       14,  // 3680 "B31B" "SDBR" "RRE" 14
		       14,  // 3690 "B31C" "MDBR" "RRE" 14
		       14,  // 3700 "B31D" "DDBR" "RRE" 14
		       15,  // 3710 "B31E" "MADBR" "RRF1" 15
		       15,  // 3720 "B31F" "MSDBR" "RRF1" 15
		       14,  // 3730 "B324" "LDER" "RRE" 14
		       14,  // 3740 "B325" "LXDR" "RRE" 14
		       14,  // 3750 "B326" "LXER" "RRE" 14
		       15,  // 3760 "B32E" "MAER" "RRF1" 15
		       15,  // 3770 "B32F" "MSER" "RRF1" 15
		       14,  // 3780 "B336" "SQXR" "RRE" 14
		       14,  // 3790 "B337" "MEER" "RRE" 14
		       15,  //      "B338" "MAYLR" "RRF1" 15 Z9-4
		       15,  //      "B339" "MYLR" "RRF1" 15 Z9-5
		       15,  //      "B33A" "MAYR" "RRF1" 15 Z9-6
		       15,  //      "B33B" "MYR" "RRF1" 15 Z9-7
		       15,  //      "B33C" "MAYHR" "RRF1" 15 Z9-8
		       15,  //      "B33D" "MYHR" "RRF1" 15 Z9-9
		       15,  // 3800 "B33E" "MADR" "RRF1" 15
		       15,  // 3810 "B33F" "MSDR" "RRF1" 15
		       14,  // 3820 "B340" "LPXBR" "RRE" 14
		       14,  // 3830 "B341" "LNXBR" "RRE" 14
		       14,  // 3840 "B342" "LTXBR" "RRE" 14
		       14,  // 3850 "B343" "LCXBR" "RRE" 14
		       14,  // 3860 "B344" "LEDBR" "RRE" 14
		       14,  // 3870 "B345" "LDXBR" "RRE" 14
		       14,  // 3880 "B346" "LEXBR" "RRE" 14
		       34,  // 3890 "B347" "FIXBR" "RRF2" 34
		       14,  // 3900 "B348" "KXBR" "RRE" 14
		       14,  // 3910 "B349" "CXBR" "RRE" 14
		       14,  // 3920 "B34A" "AXBR" "RRE" 14
		       14,  // 3930 "B34B" "SXBR" "RRE" 14
		       14,  // 3940 "B34C" "MXBR" "RRE" 14
		       14,  // 3950 "B34D" "DXBR" "RRE" 14
		       34,  // 3960 "B350" "TBEDR" "RRF2" 34
		       34,  // 3970 "B351" "TBDR" "RRF2" 34
		       30,  // 3980 "B353" "DIEBR" "RRF3" 30
		       34,  // 3990 "B357" "FIEBR" "RRF2" 34
		       14,  // 4000 "B358" "THDER" "RRE" 14
		       14,  // 4010 "B359" "THDR" "RRE" 14
		       30,  // 4020 "B35B" "DIDBR" "RRF3" 30
		       34,  // 4030 "B35F" "FIDBR" "RRF2" 34
		       14,  // 4040 "B360" "LPXR" "RRE" 14
		       14,  // 4050 "B361" "LNXR" "RRE" 14
		       14,  // 4060 "B362" "LTXR" "RRE" 14
		       14,  // 4070 "B363" "LCXR" "RRE" 14
		       14,  // 4080 "B365" "LXR" "RRE" 14
		       14,  // 4090 "B366" "LEXR" "RRE" 14
		       14,  // 4100 "B367" "FIXR" "RRE" 14
		       14,  // 4110 "B369" "CXR" "RRE" 14
		       14,  // 4120 "B374" "LZER" "RRE" 14
		       14,  // 4130 "B375" "LZDR" "RRE" 14
		       14,  // 4140 "B376" "LZXR" "RRE" 14
		       14,  // 4150 "B377" "FIER" "RRE" 14
		       14,  // 4160 "B37F" "FIDR" "RRE" 14
		       14,  // 4170 "B384" "SFPC" "RRE" 14
		       14,  // 4180 "B38C" "EFPC" "RRE" 14
		       14,  // 4190 "B394" "CEFBR" "RRE" 14
		       14,  // 4200 "B395" "CDFBR" "RRE" 14
		       14,  // 4210 "B396" "CXFBR" "RRE" 14
		       34,  // 4220 "B398" "CFEBR" "RRF2" 34
		       34,  // 4230 "B399" "CFDBR" "RRF2" 34
		       34,  // 4240 "B39A" "CFXBR" "RRF2" 34
		       14,  // 4250 "B3A4" "CEGBR" "RRE" 14
		       14,  // 4260 "B3A5" "CDGBR" "RRE" 14
		       14,  // 4270 "B3A6" "CXGBR" "RRE" 14
		       34,  // 4280 "B3A8" "CGEBR" "RRF2" 34
		       34,  // 4290 "B3A9" "CGDBR" "RRF2" 34
		       34,  // 4300 "B3AA" "CGXBR" "RRF2" 34
		       14,  // 4310 "B3B4" "CEFR" "RRE" 14
		       14,  // 4320 "B3B5" "CDFR" "RRE" 14
		       14,  // 4330 "B3B6" "CXFR" "RRE" 14
		       34,  // 4340 "B3B8" "CFER" "RRF2" 34
		       34,  // 4350 "B3B9" "CFDR" "RRF2" 34
		       34,  // 4360 "B3BA" "CFXR" "RRF2" 34
		       14,  // 4370 "B3C4" "CEGR" "RRE" 14
		       14,  // 4380 "B3C5" "CDGR" "RRE" 14
		       14,  // 4390 "B3C6" "CXGR" "RRE" 14
		       34,  // 4400 "B3C8" "CGER" "RRF2" 34
		       34,  // 4410 "B3C9" "CGDR" "RRF2" 34
		       34,  // 4420 "B3CA" "CGXR" "RRF2" 34
		       10,  // 4430 "B6" "STCTL" "RS" 10
		       10,  // 4440 "B7" "LCTL" "RS" 10
		       14,  // 4450 "B900" "LPGR" "RRE" 14
		       14,  // 4460 "B901" "LNGR" "RRE" 14
		       14,  // 4470 "B902" "LTGR" "RRE" 14
		       14,  // 4480 "B903" "LCGR" "RRE" 14
		       14,  // 4490 "B904" "LGR" "RRE" 14
		       14,  // 4500 "B905" "LURAG" "RRE" 14
		       14,  //      "B906" "LGBR" "RRE" 14 Z9-10
		       14,  //      "B907" "LGHR" "RRE" 14 Z9-11
		       14,  // 4510 "B908" "AGR" "RRE" 14
		       14,  // 4520 "B909" "SGR" "RRE" 14
		       14,  // 4530 "B90A" "ALGR" "RRE" 14
		       14,  // 4540 "B90B" "SLGR" "RRE" 14
		       14,  // 4550 "B90C" "MSGR" "RRE" 14
		       14,  // 4560 "B90D" "DSGR" "RRE" 14
		       14,  // 4570 "B90E" "EREGG" "RRE" 14
		       14,  // 4580 "B90F" "LRVGR" "RRE" 14
		       14,  // 4590 "B910" "LPGFR" "RRE" 14
		       14,  // 4600 "B911" "LNGFR" "RRE" 14
		       14,  // 4610 "B912" "LTGFR" "RRE" 14
		       14,  // 4620 "B913" "LCGFR" "RRE" 14
		       14,  // 4630 "B914" "LGFR" "RRE" 14
		       14,  // 4640 "B916" "LLGFR" "RRE" 14
		       14,  // 4650 "B917" "LLGTR" "RRE" 14
		       14,  // 4660 "B918" "AGFR" "RRE" 14
		       14,  // 4670 "B919" "SGFR" "RRE" 14
		       14,  // 4680 "B91A" "ALGFR" "RRE" 14
		       14,  // 4690 "B91B" "SLGFR" "RRE" 14
		       14,  // 4700 "B91C" "MSGFR" "RRE" 14
		       14,  // 4710 "B91D" "DSGFR" "RRE" 14
		       14,  // 4720 "B91E" "KMAC" "RRE" 14
		       14,  // 4730 "B91F" "LRVR" "RRE" 14
		       14,  // 4740 "B920" "CGR" "RRE" 14
		       14,  // 4750 "B921" "CLGR" "RRE" 14
		       14,  // 4760 "B925" "STURG" "RRE" 14
		       14,  //      "B926" "LBR" "RRE" 14 Z9-12
		       14,  //      "B927" "LHR" "RRE" 14 Z9-13
		       14,  // 4770 "B92E" "KM" "RRE" 14
		       14,  // 4780 "B92F" "KMC" "RRE" 14
		       14,  // 4790 "B930" "CGFR" "RRE" 14
		       14,  // 4800 "B931" "CLGFR" "RRE" 14
		       14,  // 4810 "B93E" "KIMD" "RRE" 14
		       14,  // 4820 "B93F" "KLMD" "RRE" 14
		       14,  // 4830 "B946" "BCTGR" "RRE" 14
		       14,  // 4840 "B980" "NGR" "RRE" 14
		       14,  // 4850 "B981" "OGR" "RRE" 14
		       14,  // 4860 "B982" "XGR" "RRE" 14
		       14,  //      "B983" "FLOGR" "RRE" 14 Z9-14
		       14,  //      "B984" "LLGCR" "RRE" 14 Z9-15
		       14,  //      "B985" "LLGHR" "RRE" 14 Z9-16
		       14,  // 4870 "B986" "MLGR" "RRE" 14
		       14,  // 4880 "B987" "DLGR" "RRE" 14
		       14,  // 4890 "B988" "ALCGR" "RRE" 14
		       14,  // 4900 "B989" "SLBGR" "RRE" 14
		       14,  // 4910 "B98A" "CSPG" "RRE" 14
		       14,  // 4920 "B98D" "EPSW" "RRE" 14
		       34,  // 4930 "B98E" "IDTE" "RRF2" 34
		       14,  // 4940 "B990" "TRTT" "RRE" 14
		       14,  // 4950 "B991" "TRTO" "RRE" 14
		       14,  // 4960 "B992" "TROT" "RRE" 14
		       14,  // 4970 "B993" "TROO" "RRE" 14
		       14,  //      "B994" "LLCR" "RRE" 14 Z9-17
		       14,  //      "B995" "LLHR" "RRE" 14 Z9-18
		       14,  // 4980 "B996" "MLR" "RRE" 14
		       14,  // 4990 "B997" "DLR" "RRE" 14
		       14,  // 5000 "B998" "ALCR" "RRE" 14
		       14,  // 5010 "B999" "SLBR" "RRE" 14
		       14,  // 5020 "B99A" "EPAIR" "RRE" 14
		       14,  // 5030 "B99B" "ESAIR" "RRE" 14
		       14,  // 5040 "B99D" "ESEA" "RRE" 14
		       14,  // 5050 "B99E" "PTI" "RRE" 14
		       14,  // 5060 "B99F" "SSAIR" "RRE" 14
		       14,  //      "B9AA" "LPTEA" "RRE" 14 Z9-19
		       14,  // 5070 "B9B0" "CU14" "RRE" 14
		       14,  // 5080 "B9B1" "CU24" "RRE" 14
		       14,  // 5090 "B9B2" "CU41" "RRE" 14
		       14,  // 5100 "B9B3" "CU42" "RRE" 14
		       14,  // 5110 "B9BE" "SRSTU" "RRE" 14
		       10,  // 5120 "BA" "CS" "RS" 10
		       10,  // 5130 "BB" "CDS" "RS" 10
		       10,  // 5140 "BD" "CLM" "RS" 10
		       10,  // 5150 "BE" "STCM" "RS" 10
		       10,  // 5160 "BF" "ICM" "RS" 10
		       16,  // 5170 "C00" "LARL" "RIL" 16
		       16,  //      "C01" "LGFI" "RIL" 16 Z9-20
		       16,  // 5180 "C04" "BRCL" "RIL" 16
		       33,  // 5390 "C040" "JLNOP" "BLX" 33
		       33,  // 5400 "C041" "BROL" "BLX" 33
		       33,  // 5410 "C041" "JLO" "BLX" 33
		       33,  // 5420 "C042" "BRHL" "BLX" 33
		       33,  // 5430 "C042" "BRPL" "BLX" 33
		       33,  // 5440 "C042" "JLH" "BLX" 33
		       33,  // 5450 "C042" "JLP" "BLX" 33
		       33,  // 5460 "C044" "BRLL" "BLX" 33
		       33,  // 5470 "C044" "BRML" "BLX" 33
		       33,  // 5480 "C044" "JLL" "BLX" 33
		       33,  // 5490 "C044" "JLM" "BLX" 33
		       33,  // 5500 "C047" "BRNEL" "BLX" 33
		       33,  // 5510 "C047" "BRNZL" "BLX" 33
		       33,  // 5520 "C047" "JLNE" "BLX" 33
		       33,  // 5530 "C047" "JLNZ" "BLX" 33
		       33,  // 5540 "C048" "BREL" "BLX" 33
		       33,  // 5550 "C048" "BRZL" "BLX" 33
		       33,  // 5560 "C048" "JLE" "BLX" 33
		       33,  // 5570 "C048" "JLZ" "BLX" 33
		       33,  // 5580 "C04B" "BRNLL" "BLX" 33
		       33,  // 5590 "C04B" "BRNML" "BLX" 33
		       33,  // 5600 "C04B" "JLNL" "BLX" 33
		       33,  // 5610 "C04B" "JLNM" "BLX" 33
		       33,  // 5620 "C04D" "BRNHL" "BLX" 33
		       33,  // 5630 "C04D" "BRNPL" "BLX" 33
		       33,  // 5640 "C04D" "JLNH" "BLX" 33
		       33,  // 5650 "C04D" "JLNP" "BLX" 33
		       33,  // 5660 "C04E" "BRNOL" "BLX" 33
		       33,  // 5670 "C04E" "JLNO" "BLX" 33
		       33,  // 5680 "C04F" "BRUL" "BLX" 33
		       33,  // 5690 "C04F" "JLU" "BLX" 33
		       16,  // 5210 "C05" "BRASL" "RIL" 16
		       16,  // 5220 "C05" "JASL" "RIL" 16
		       16,  //      "C06" "XIHF" "RIL" 16 Z9-21
		       16,  //      "C07" "XILF" "RIL" 16 Z9-22
		       16,  //      "C08" "IIHF" "RIL" 16 Z9-23
		       16,  //      "C09" "IILF" "RIL" 16 Z9-24
		       16,  //      "C0A" "NIHF" "RIL" 16 Z9-25
		       16,  //      "C0B" "NILF" "RIL" 16 Z9-26
		       16,  //      "C0C" "OIHF" "RIL" 16 Z9-27
		       16,  //      "C0D" "OILF" "RIL" 16 Z9-28
		       16,  //      "C0E" "LLIHF" "RIL" 16 Z9-29
		       16,  //      "C0F" "LLILF" "RIL" 16 Z9-30
		       16,  //      "C24" "SLGFI" "RIL" 16 Z9-31
		       16,  //      "C25" "SLFI" "RIL" 16 Z9-32
		       16,  //      "C28" "AGFI" "RIL" 16 Z9-33
		       16,  //      "C29" "AFI" "RIL" 16 Z9-34
		       16,  //      "C2A" "ALGFI" "RIL" 16 Z9-35
		       16,  //      "C2B" "ALFI" "RIL" 16 Z9-36
		       16,  //      "C2C" "CGFI" "RIL" 16 Z9-37
		       16,  //      "C2D" "CFI" "RIL" 16 Z9-38
		       16,  //      "C2E" "CLGFI" "RIL" 16 Z9-39
		       16,  //      "C2F" "CLFI" "RIL" 16 Z9-40
		       32,  //      "C80" "MVCOS" "SSF" 32 Z9-41		       
		       17,  // 5230 "D0" "TRTR" "SS" 17
		       17,  // 5240 "D1" "MVN" "SS" 17
		       17,  // 5250 "D2" "MVC" "SS" 17
		       17,  // 5260 "D3" "MVZ" "SS" 17
		       17,  // 5270 "D4" "NC" "SS" 17
		       17,  // 5280 "D5" "CLC" "SS" 17
		       17,  // 5290 "D6" "OC" "SS" 17
		       17,  // 5300 "D7" "XC" "SS" 17
		       17,  // 5310 "D9" "MVCK" "SS" 17
		       17,  // 5320 "DA" "MVCP" "SS" 17
		       17,  // 5330 "DB" "MVCS" "SS" 17
		       17,  // 5340 "DC" "TR" "SS" 17
		       17,  // 5350 "DD" "TRT" "SS" 17
		       17,  // 5360 "DE" "ED" "SS" 17
		       17,  // 5370 "DF" "EDMK" "SS" 17
		       17,  // 5380 "E1" "PKU" "SS" 17
		       17,  // 5390 "E2" "UNPKU" "SS" 17
		       18,  //      "E302" "LTG" "RXY" 18 Z9-42
		       18,  // 5400 "E303" "LRAG" "RXY" 18
		       18,  // 5410 "E304" "LG" "RXY" 18
		       18,  // 5420 "E306" "CVBY" "RXY" 18
		       18,  // 5430 "E308" "AG" "RXY" 18
		       18,  // 5440 "E309" "SG" "RXY" 18
		       18,  // 5450 "E30A" "ALG" "RXY" 18
		       18,  // 5460 "E30B" "SLG" "RXY" 18
		       18,  // 5470 "E30C" "MSG" "RXY" 18
		       18,  // 5480 "E30D" "DSG" "RXY" 18
		       18,  // 5490 "E30E" "CVBG" "RXY" 18
		       18,  // 5500 "E30F" "LRVG" "RXY" 18
		       18,  //      "E312" "LT" "RXY" 18 Z9-43
		       18,  // 5510 "E313" "LRAY" "RXY" 18
		       18,  // 5520 "E314" "LGF" "RXY" 18
		       18,  // 5530 "E315" "LGH" "RXY" 18
		       18,  // 5540 "E316" "LLGF" "RXY" 18
		       18,  // 5550 "E317" "LLGT" "RXY" 18
		       18,  // 5560 "E318" "AGF" "RXY" 18
		       18,  // 5570 "E319" "SGF" "RXY" 18
		       18,  // 5580 "E31A" "ALGF" "RXY" 18
		       18,  // 5590 "E31B" "SLGF" "RXY" 18
		       18,  // 5600 "E31C" "MSGF" "RXY" 18
		       18,  // 5610 "E31D" "DSGF" "RXY" 18
		       18,  // 5620 "E31E" "LRV" "RXY" 18
		       18,  // 5630 "E31F" "LRVH" "RXY" 18
		       18,  // 5640 "E320" "CG" "RXY" 18
		       18,  // 5650 "E321" "CLG" "RXY" 18
		       18,  // 5660 "E324" "STG" "RXY" 18
		       18,  // 5670 "E326" "CVDY" "RXY" 18
		       18,  // 5680 "E32E" "CVDG" "RXY" 18
		       18,  // 5690 "E32F" "STRVG" "RXY" 18
		       18,  // 5700 "E330" "CGF" "RXY" 18
		       18,  // 5710 "E331" "CLGF" "RXY" 18
		       18,  // 5720 "E33E" "STRV" "RXY" 18
		       18,  // 5730 "E33F" "STRVH" "RXY" 18
		       18,  // 5740 "E346" "BCTG" "RXY" 18
		       18,  // 5750 "E350" "STY" "RXY" 18
		       18,  // 5760 "E351" "MSY" "RXY" 18
		       18,  // 5770 "E354" "NY" "RXY" 18
		       18,  // 5780 "E355" "CLY" "RXY" 18
		       18,  // 5790 "E356" "OY" "RXY" 18
		       18,  // 5800 "E357" "XY" "RXY" 18
		       18,  // 5810 "E358" "LY" "RXY" 18
		       18,  // 5820 "E359" "CY" "RXY" 18
		       18,  // 5830 "E35A" "AY" "RXY" 18
		       18,  // 5840 "E35B" "SY" "RXY" 18
		       18,  // 5850 "E35E" "ALY" "RXY" 18
		       18,  // 5860 "E35F" "SLY" "RXY" 18
		       18,  // 5870 "E370" "STHY" "RXY" 18
		       18,  // 5880 "E371" "LAY" "RXY" 18
		       18,  // 5890 "E372" "STCY" "RXY" 18
		       18,  // 5900 "E373" "ICY" "RXY" 18
		       18,  // 5910 "E376" "LB" "RXY" 18
		       18,  // 5920 "E377" "LGB" "RXY" 18
		       18,  // 5930 "E378" "LHY" "RXY" 18
		       18,  // 5940 "E379" "CHY" "RXY" 18
		       18,  // 5950 "E37A" "AHY" "RXY" 18
		       18,  // 5960 "E37B" "SHY" "RXY" 18
		       18,  // 5970 "E380" "NG" "RXY" 18
		       18,  // 5980 "E381" "OG" "RXY" 18
		       18,  // 5990 "E382" "XG" "RXY" 18
		       18,  // 6000 "E386" "MLG" "RXY" 18
		       18,  // 6010 "E387" "DLG" "RXY" 18
		       18,  // 6020 "E388" "ALCG" "RXY" 18
		       18,  // 6030 "E389" "SLBG" "RXY" 18
		       18,  // 6040 "E38E" "STPQ" "RXY" 18
		       18,  // 6050 "E38F" "LPQ" "RXY" 18
		       18,  // 6060 "E390" "LLGC" "RXY" 18
		       18,  // 6070 "E391" "LLGH" "RXY" 18
		       18,  //      "E394" "LLC" "RXY" 18 Z9-44
		       18,  //      "E395" "LLH" "RXY" 18 Z9-45
		       18,  // 6080 "E396" "ML" "RXY" 18
		       18,  // 6090 "E397" "DL" "RXY" 18
		       18,  // 6100 "E398" "ALC" "RXY" 18
		       18,  // 6110 "E399" "SLB" "RXY" 18
		       19,  // 6120 "E500" "LASP" "SSE" 19
		       19,  // 6130 "E501" "TPROT" "SSE" 19
		       19,  // 6140 "E502" "STRAG" "SSE" 19
		       19,  // 6150 "E50E" "MVCSK" "SSE" 19
		       19,  // 6160 "E50F" "MVCDK" "SSE" 19
		       17,  // 6170 "E8" "MVCIN" "SS" 17
		       31,  // 6180 "E9" "PKA" "SS" 31
		       17,  // 6190 "EA" "UNPKA" "SS" 17
		       20,  // 6200 "EB04" "LMG" "RSY" 20
		       20,  // 6210 "EB0A" "SRAG" "RSY" 20
		       20,  // 6220 "EB0B" "SLAG" "RSY" 20
		       20,  // 6230 "EB0C" "SRLG" "RSY" 20
		       20,  // 6240 "EB0D" "SLLG" "RSY" 20
		       20,  // 6250 "EB0F" "TRACG" "RSY" 20
		       20,  // 6260 "EB14" "CSY" "RSY" 20
		       20,  // 6270 "EB1C" "RLLG" "RSY" 20
		       20,  // 6280 "EB1D" "RLL" "RSY" 20
		       20,  // 6290 "EB20" "CLMH" "RSY" 20
		       20,  // 6300 "EB21" "CLMY" "RSY" 20
		       20,  // 6310 "EB24" "STMG" "RSY" 20
		       20,  // 6320 "EB25" "STCTG" "RSY" 20
		       20,  // 6330 "EB26" "STMH" "RSY" 20
		       20,  // 6340 "EB2C" "STCMH" "RSY" 20
		       20,  // 6350 "EB2D" "STCMY" "RSY" 20
		       20,  // 6360 "EB2F" "LCTLG" "RSY" 20
		       20,  // 6370 "EB30" "CSG" "RSY" 20
		       20,  // 6380 "EB31" "CDSY" "RSY" 20
		       20,  // 6390 "EB3E" "CDSG" "RSY" 20
		       20,  // 6400 "EB44" "BXHG" "RSY" 20
		       20,  // 6410 "EB45" "BXLEG" "RSY" 20
		       21,  // 6420 "EB51" "TMY" "SIY" 21
		       21,  // 6430 "EB52" "MVIY" "SIY" 21
		       21,  // 6440 "EB54" "NIY" "SIY" 21
		       21,  // 6450 "EB55" "CLIY" "SIY" 21
		       21,  // 6460 "EB56" "OIY" "SIY" 21
		       21,  // 6470 "EB57" "XIY" "SIY" 21
		       20,  // 6480 "EB80" "ICMH" "RSY" 20
		       20,  // 6490 "EB81" "ICMY" "RSY" 20
		       20,  // 6500 "EB8E" "MVCLU" "RSY" 20
		       20,  // 6510 "EB8F" "CLCLU" "RSY" 20
		       20,  // 6520 "EB90" "STMY" "RSY" 20
		       20,  // 6530 "EB96" "LMH" "RSY" 20
		       20,  // 6540 "EB98" "LMY" "RSY" 20
		       20,  // 6550 "EB9A" "LAMY" "RSY" 20
		       20,  // 6560 "EB9B" "STAMY" "RSY" 20
		       22,  // 6570 "EBC0" "TP" "RSL" 22
		       23,  // 6580 "EC44" "BRXHG" "RIE" 23
		       23,  // 6590 "EC44" "JXHG" "RIE" 23
		       23,  // 6600 "EC45" "BRXLG" "RIE" 23
		       23,  // 6610 "EC45" "JXLEG" "RIE" 23
		       24,  // 6620 "ED04" "LDEB" "RXE" 24
		       24,  // 6630 "ED05" "LXDB" "RXE" 24
		       24,  // 6640 "ED06" "LXEB" "RXE" 24
		       24,  // 6650 "ED07" "MXDB" "RXE" 24
		       24,  // 6660 "ED08" "KEB" "RXE" 24
		       24,  // 6670 "ED09" "CEB" "RXE" 24
		       24,  // 6680 "ED0A" "AEB" "RXE" 24
		       24,  // 6690 "ED0B" "SEB" "RXE" 24
		       24,  // 6700 "ED0C" "MDEB" "RXE" 24
		       24,  // 6710 "ED0D" "DEB" "RXE" 24
		       25,  // 6720 "ED0E" "MAEB" "RXF" 25
		       25,  // 6730 "ED0F" "MSEB" "RXF" 25
		       24,  // 6740 "ED10" "TCEB" "RXE" 24
		       24,  // 6750 "ED11" "TCDB" "RXE" 24
		       24,  // 6760 "ED12" "TCXB" "RXE" 24
		       24,  // 6770 "ED14" "SQEB" "RXE" 24
		       24,  // 6780 "ED15" "SQDB" "RXE" 24
		       24,  // 6790 "ED17" "MEEB" "RXE" 24
		       24,  // 6800 "ED18" "KDB" "RXE" 24
		       24,  // 6810 "ED19" "CDB" "RXE" 24
		       24,  // 6820 "ED1A" "ADB" "RXE" 24
		       24,  // 6830 "ED1B" "SDB" "RXE" 24
		       24,  // 6840 "ED1C" "MDB" "RXE" 24
		       24,  // 6850 "ED1D" "DDB" "RXE" 24
		       25,  // 6860 "ED1E" "MADB" "RXF" 25
		       25,  // 6870 "ED1F" "MSDB" "RXF" 25
		       24,  // 6880 "ED24" "LDE" "RXE" 24
		       24,  // 6890 "ED25" "LXD" "RXE" 24
		       24,  // 6900 "ED26" "LXE" "RXE" 24
		       25,  // 6910 "ED2E" "MAE" "RXF" 25
		       25,  // 6920 "ED2F" "MSE" "RXF" 25
		       24,  // 6930 "ED34" "SQE" "RXE" 24
		       24,  // 6940 "ED35" "SQD" "RXE" 24
		       24,  // 6950 "ED37" "MEE" "RXE" 24
		       25,  //      "ED38" "MAYL" "RXF" 25 Z9-46
		       25,  //      "ED39" "MYL" "RXF" 25 Z9-47
		       25,  //      "ED3A" "MAY" "RXF" 25 Z9-48
		       25,  //      "EDEB" "MY" "RXF" 25 Z9-49
		       25,  //      "ED3C" "MAYH" "RXF" 25 Z9-50
		       25,  //      "EDED" "MYH" "RXF" 25 Z9-51
		       25,  // 6960 "ED3E" "MAD" "RXF" 25
		       25,  // 6970 "ED3F" "MSD" "RXF" 25
		       18,  // 6980 "ED64" "LEY" "RXY" 18
		       18,  // 6990 "ED65" "LDY" "RXY" 18
		       18,  // 7000 "ED66" "STEY" "RXY" 18
		       18,  // 7010 "ED67" "STDY" "RXY" 18
		       27,  // 7020 "EE" "PLO" "SS3" 27
		       28,  // 7030 "EF" "LMD" "SS4" 28
		       29,  // 7040 "F0" "SRP" "SS5" 29
		       26,  // 7050 "F1" "MVO" "SS2" 26
		       26,  // 7060 "F2" "PACK" "SS2" 26
		       26,  // 7070 "F3" "UNPK" "SS2" 26
		       26,  // 7080 "F8" "ZAP" "SS2" 26
		       26,  // 7090 "F9" "CP" "SS2" 26
		       26,  // 7100 "FA" "AP" "SS2" 26
		       26,  // 7110 "FB" "SP" "SS2" 26
		       26,  // 7120 "FC" "MP" "SS2" 26
		       26,  // 7130 "FD" "DP" "SS2" 26
		       101,  // 7140  "CCW"  101
		       102,  // 7150  "CCW0"  102
		       103,  // 7160  "CCW1"  103
		       104,  // 7170  "DC"  104
		       105,  // 7180  "DS"  105
		       106,  // 7190  "ALIAS"  106
		       107,  // 7200  "AMODE"  107
		       108,  // 7210  "CATTR"  108
		       109,  // 7220  "COM"  109
		       110,  // 7230  "CSECT"  110
		       111,  // 7240  "CXD"  111
		       112,  // 7250  "DSECT"  112
		       113,  // 7260  "DXD"  113
		       114,  // 7270  "ENTRY"  114
		       115,  // 7280  "EXTRN"  115
		       116,  // 7290  "LOCTR"  116
		       117,  // 7300  "RMODE"  117
		       118,  // 7310  "RSECT"  118
		       119,  // 7320  "START"  119
		       120,  // 7330  "WXTRN"  120
		       121,  // 7340  "XATTR"  121
		       122,  // 7350  ""  122
		       123,  // 7360  "DROP"  123
		       124,  // 7370  "USING"  124
		       125,  // 7380  "AEJECT"  125
		       126,  // 7390  "ASPACE"  126
		       127,  // 7400  "CEJECT"  127
		       128,  // 7410  "EJECT"  128
		       129,  // 7420  "PRINT"  129
		       130,  // 7430  "SPACE"  130
		       131,  // 7440  "TITLE"  131
		       132,  // 7450  "ADATA"  132
		       133,  // 7460  "CNOP"  133
		       224,  // 7470  "COPY"  134
		       135,  // 7480  "END"  135
		       136,  // 7490  "EQU"  136
		       137,  // 7500  "EXITCTL"  137
		       138,  // 7510  "ICTL"  138
		       139,  // 7520  "ISEQ"  139
		       140,  // 7530  "LTORG"  140
		       225,  // 7540  "OPSYN"  141 //RPI150
		       142,  // 7550  "ORG"  142
		       143,  // 7560  "POP"  143
		       223,  // 7570  "PUNCH"  144
		       145,  // 7580  "PUSH"  145
		       146,  // 7590  "REPRO"  146
		       201,  // 7600  "ACTR"  147
		       202,  // 7610  "AGO"  148
		       203,  // 7620  "AIF"  149
		       204,  // 7630  "AINSERT"  150
		       205,  // 7640  "ANOP"  151
		       206,  // 7650  "AREAD"  152
		       207,  // 7660  "GBLA"  153
		       208,  // 7670  "GBLB"  154
		       209,  // 7680  "GBLC"  155
		       210,  // 7690  "LCLA"  156
		       211,  // 7700  "LCLB"  157
		       212,  // 7710  "LCLC"  158
		       213,  // 7720  "MHELP"  159
		       214,  // 7730  "MNOTE"  160
		       215,  // 7740  "SETA"  161
		       216,  // 7750  "SETAF"  162
		       217,  // 7760  "SETB"  163
		       218,  // 7770  "SETC"  164
		       219,  // 7780  "SETCF"  165
		       220,  // 7790  "MACRO"  166
		       221,  // 7800  "MEND"  167
		       222   // 7810  "MEXIT"  168		       
  	           };  	
      String[]   op_code = {
    		   "??",    // 00 comments
		       "0101",  // 10 "0101" "PR" "E" 1
		       "0102",  // 20 "0102" "UPT" "E" 1
		       "0104",  //    "0104" "PTFF" "E" 1 Z9-1
		       "0107",  // 30 "0107" "SCKPF" "E" 1
		       "010B",  // 40 "010B" "TAM" "E" 1
		       "010C",  // 50 "010C" "SAM24" "E" 1
		       "010D",  // 60 "010D" "SAM31" "E" 1
		       "010E",  // 70 "010E" "SAM64" "E" 1
		       "01FF",  // 80 "01FF" "TRAP2" "E" 1
		       "04",  // 90 "04" "SPM" "RR" 2
		       "05",  // 100 "05" "BALR" "RR" 2
		       "06",  // 110 "06" "BCTR" "RR" 2
		       "07",  // 120 "07" "BCR" "RR" 2
		       "07F",  // 130 "07F" "BR" "BRX" 3
		       "070",  // 140 "070" "NOPR" "BRX" 3
		       "072",  // 150 "072" "BHR" "BRX" 3
		       "074",  // 160 "074" "BLR" "BRX" 3
		       "078",  // 170 "078" "BER" "BRX" 3
		       "07D",  // 180 "07D" "BNHR" "BRX" 3
		       "07B",  // 190 "07B" "BNLR" "BRX" 3
		       "077",  // 200 "077" "BNER" "BRX" 3
		       "072",  // 210 "072" "BPR" "BRX" 3
		       "071",  // 220 "071" "BOR" "BRX" 3
		       "074",  // 230 "074" "BMR" "BRX" 3
		       "078",  // 240 "078" "BZR" "BRX" 3
		       "07D",  // 250 "07D" "BNPR" "BRX" 3
		       "07B",  // 260 "07B" "BNMR" "BRX" 3
		       "077",  // 270 "077" "BNZR" "BRX" 3
		       "07E",  // 280 "07E" "BNOR" "BRX" 3
		       "0A",  // 290 "0A" "SVC" "I" 4
		       "0B",  // 300 "0B" "BSM" "RR" 2
		       "0C",  // 310 "0C" "BASSM" "RR" 2
		       "0D",  // 320 "0D" "BASR" "RR" 2
		       "0E",  // 330 "0E" "MVCL" "RR" 2
		       "0F",  // 340 "0F" "CLCL" "RR" 2
		       "10",  // 350 "10" "LPR" "RR" 2
		       "11",  // 360 "11" "LNR" "RR" 2
		       "12",  // 370 "12" "LTR" "RR" 2
		       "13",  // 380 "13" "LCR" "RR" 2
		       "14",  // 390 "14" "NR" "RR" 2
		       "15",  // 400 "15" "CLR" "RR" 2
		       "16",  // 410 "16" "OR" "RR" 2
		       "17",  // 420 "17" "XR" "RR" 2
		       "18",  // 430 "18" "LR" "RR" 2
		       "19",  // 440 "19" "CR" "RR" 2
		       "1A",  // 450 "1A" "AR" "RR" 2
		       "1B",  // 460 "1B" "SR" "RR" 2
		       "1C",  // 470 "1C" "MR" "RR" 2
		       "1D",  // 480 "1D" "DR" "RR" 2
		       "1E",  // 490 "1E" "ALR" "RR" 2
		       "1F",  // 500 "1F" "SLR" "RR" 2
		       "20",  // 510 "20" "LPDR" "RR" 2
		       "21",  // 520 "21" "LNDR" "RR" 2
		       "22",  // 530 "22" "LTDR" "RR" 2
		       "23",  // 540 "23" "LCDR" "RR" 2
		       "24",  // 550 "24" "HDR" "RR" 2
		       "25",  // 560 "25" "LDXR" "RR" 2
		       "25",  // 570 "25" "LRDR" "RR" 2
		       "26",  // 580 "26" "MXR" "RR" 2
		       "27",  // 590 "27" "MXDR" "RR" 2
		       "28",  // 600 "28" "LDR" "RR" 2
		       "29",  // 610 "29" "CDR" "RR" 2
		       "2A",  // 620 "2A" "ADR" "RR" 2
		       "2B",  // 630 "2B" "SDR" "RR" 2
		       "2C",  // 640 "2C" "MDR" "RR" 2
		       "2D",  // 650 "2D" "DDR" "RR" 2
		       "2E",  // 660 "2E" "AWR" "RR" 2
		       "2F",  // 670 "2F" "SWR" "RR" 2
		       "30",  // 680 "30" "LPER" "RR" 2
		       "31",  // 690 "31" "LNER" "RR" 2
		       "32",  // 700 "32" "LTER" "RR" 2
		       "33",  // 710 "33" "LCER" "RR" 2
		       "34",  // 720 "34" "HER" "RR" 2
		       "35",  // 730 "35" "LEDR" "RR" 2
		       "35",  // 740 "35" "LRER" "RR" 2
		       "36",  // 750 "36" "AXR" "RR" 2
		       "37",  // 760 "37" "SXR" "RR" 2
		       "38",  // 770 "38" "LER" "RR" 2
		       "39",  // 780 "39" "CER" "RR" 2
		       "3A",  // 790 "3A" "AER" "RR" 2
		       "3B",  // 800 "3B" "SER" "RR" 2
		       "3C",  // 810 "3C" "MDER" "RR" 2
		       "3C",  // 820 "3C" "MER" "RR" 2
		       "3D",  // 830 "3D" "DER" "RR" 2
		       "3E",  // 840 "3E" "AUR" "RR" 2
		       "3F",  // 850 "3F" "SUR" "RR" 2
		       "40",  // 860 "40" "STH" "RX" 5
		       "41",  // 870 "41" "LA" "RX" 5
		       "42",  // 880 "42" "STC" "RX" 5
		       "43",  // 890 "43" "IC" "RX" 5
		       "44",  // 900 "44" "EX" "RX" 5
		       "45",  // 910 "45" "BAL" "RX" 5
		       "46",  // 920 "46" "BCT" "RX" 5
		       "47",  // 930 "47" "BC" "RX" 5
		       "47F",  // 940 "47F" "B" "BCX" 6
		       "470",  // 950 "470" "NOP" "BCX" 6
		       "472",  // 960 "472" "BH" "BCX" 6
		       "474",  // 970 "474" "BL" "BCX" 6
		       "478",  // 980 "478" "BE" "BCX" 6
		       "47D",  // 990 "47D" "BNH" "BCX" 6
		       "47B",  // 1000 "47B" "BNL" "BCX" 6
		       "477",  // 1010 "477" "BNE" "BCX" 6
		       "472",  // 1020 "472" "BP" "BCX" 6
		       "471",  // 1030 "471" "BO" "BCX" 6
		       "474",  // 1040 "474" "BM" "BCX" 6
		       "478",  // 1050 "478" "BZ" "BCX" 6
		       "47D",  // 1060 "47D" "BNP" "BCX" 6
		       "47B",  // 1070 "47B" "BNM" "BCX" 6
		       "477",  // 1080 "477" "BNZ" "BCX" 6
		       "47E",  // 1090 "47E" "BNO" "BCX" 6
		       "48",  // 1100 "48" "LH" "RX" 5
		       "49",  // 1110 "49" "CH" "RX" 5
		       "4A",  // 1120 "4A" "AH" "RX" 5
		       "4B",  // 1130 "4B" "SH" "RX" 5
		       "4C",  // 1140 "4C" "MH" "RX" 5
		       "4D",  // 1150 "4D" "BAS" "RX" 5
		       "4E",  // 1160 "4E" "CVD" "RX" 5
		       "4F",  // 1170 "4F" "CVB" "RX" 5
		       "50",  // 1180 "50" "ST" "RX" 5
		       "51",  // 1190 "51" "LAE" "RX" 5
		       "54",  // 1200 "54" "N" "RX" 5
		       "55",  // 1210 "55" "CL" "RX" 5
		       "56",  // 1220 "56" "O" "RX" 5
		       "57",  // 1230 "57" "X" "RX" 5
		       "58",  // 1240 "58" "L" "RX" 5
		       "59",  // 1250 "59" "C" "RX" 5
		       "5A",  // 1260 "5A" "A" "RX" 5
		       "5B",  // 1270 "5B" "S" "RX" 5
		       "5C",  // 1280 "5C" "M" "RX" 5
		       "5D",  // 1290 "5D" "D" "RX" 5
		       "5E",  // 1300 "5E" "AL" "RX" 5
		       "5F",  // 1310 "5F" "SL" "RX" 5
		       "60",  // 1320 "60" "STD" "RX" 5
		       "67",  // 1330 "67" "MXD" "RX" 5
		       "68",  // 1340 "68" "LD" "RX" 5
		       "69",  // 1350 "69" "CD" "RX" 5
		       "6A",  // 1360 "6A" "AD" "RX" 5
		       "6B",  // 1370 "6B" "SD" "RX" 5
		       "6C",  // 1380 "6C" "MD" "RX" 5
		       "6D",  // 1390 "6D" "DD" "RX" 5
		       "6E",  // 1400 "6E" "AW" "RX" 5
		       "6F",  // 1410 "6F" "SW" "RX" 5
		       "70",  // 1420 "70" "STE" "RX" 5
		       "71",  // 1430 "71" "MS" "RX" 5
		       "78",  // 1440 "78" "LE" "RX" 5
		       "79",  // 1450 "79" "CE" "RX" 5
		       "7A",  // 1460 "7A" "AE" "RX" 5
		       "7B",  // 1470 "7B" "SE" "RX" 5
		       "7C",  // 1480 "7C" "MDE" "RX" 5
		       "7C",  // 1490 "7C" "ME" "RX" 5
		       "7D",  // 1500 "7D" "DE" "RX" 5
		       "7E",  // 1510 "7E" "AU" "RX" 5
		       "7F",  // 1520 "7F" "SU" "RX" 5
		       "8000",  // 1530 "8000" "SSM" "S" 7
		       "8200",  // 1540 "8200" "LPSW" "S" 7
		       "83",  // 1550 "83" "DIAGNOSE" "DM" 8
		       "84",  // 1560 "84" "BRXH" "RSI" 9
		       "84",  // 1570 "84" "JXH" "RSI" 9
		       "85",  // 1580 "85" "BRXLE" "RSI" 9
		       "85",  // 1590 "85" "JXLE" "RSI" 9
		       "86",  // 1600 "86" "BXH" "RS" 10
		       "87",  // 1610 "87" "BXLE" "RS" 10
		       "88",  // 1620 "88" "SRL" "RS" 10
		       "89",  // 1630 "89" "SLL" "RS" 10
		       "8A",  // 1640 "8A" "SRA" "RS" 10
		       "8B",  // 1650 "8B" "SLA" "RS" 10
		       "8C",  // 1660 "8C" "SRDL" "RS" 10
		       "8D",  // 1670 "8D" "SLDL" "RS" 10
		       "8E",  // 1680 "8E" "SRDA" "RS" 10
		       "8F",  // 1690 "8F" "SLDA" "RS" 10
		       "90",  // 1700 "90" "STM" "RS" 10
		       "91",  // 1710 "91" "TM" "SI" 11
		       "92",  // 1720 "92" "MVI" "SI" 11
		       "9300",  // 1730 "9300" "TS" "S" 7
		       "94",  // 1740 "94" "NI" "SI" 11
		       "95",  // 1750 "95" "CLI" "SI" 11
		       "96",  // 1760 "96" "OI" "SI" 11
		       "97",  // 1770 "97" "XI" "SI" 11
		       "98",  // 1780 "98" "LM" "RS" 10
		       "99",  // 1790 "99" "TRACE" "RS" 10
		       "9A",  // 1800 "9A" "LAM" "RS" 10
		       "9B",  // 1810 "9B" "STAM" "RS" 10
		       "A50",  // 1820 "A50" "IIHH" "RI" 12
		       "A51",  // 1830 "A51" "IIHL" "RI" 12
		       "A52",  // 1840 "A52" "IILH" "RI" 12
		       "A53",  // 1850 "A53" "IILL" "RI" 12
		       "A54",  // 1860 "A54" "NIHH" "RI" 12
		       "A55",  // 1870 "A55" "NIHL" "RI" 12
		       "A56",  // 1880 "A56" "NILH" "RI" 12
		       "A57",  // 1890 "A57" "NILL" "RI" 12
		       "A58",  // 1900 "A58" "OIHH" "RI" 12
		       "A59",  // 1910 "A59" "OIHL" "RI" 12
		       "A5A",  // 1920 "A5A" "OILH" "RI" 12
		       "A5B",  // 1930 "A5B" "OILL" "RI" 12
		       "A5C",  // 1940 "A5C" "LLIHH" "RI" 12
		       "A5D",  // 1950 "A5D" "LLIHL" "RI" 12
		       "A5E",  // 1960 "A5E" "LLILH" "RI" 12
		       "A5F",  // 1970 "A5F" "LLILL" "RI" 12
		       "A70",  // 1980 "A70" "TMLH" "RI" 12
		       "A70",  // 1990 "A70" "TMH" "RI" 12
		       "A71",  // 2000 "A71" "TMLL" "RI" 12
		       "A71",  // 2010 "A71" "TML" "RI" 12
		       "A72",  // 2020 "A72" "TMHH" "RI" 12
		       "A73",  // 2030 "A73" "TMHL" "RI" 12
		       "A74",  // 2040 "A74" "BRC" "RI" 12
		       "A74F",  // 2050 "A74F" "J" "BRC" 13
		       "A740",  // 2060 "A740" "JNOP" "BRC" 13
		       "A74F",  // 2070 "A74F" "BRU" "BRC" 13
		       "A742",  // 2080 "A742" "BRH" "BRC" 13
		       "A744",  // 2090 "A744" "BRL" "BRC" 13
		       "A748",  // 2100 "A748" "BRE" "BRC" 13
		       "A74D",  // 2110 "A74D" "BRNH" "BRC" 13
		       "A74B",  // 2120 "A74B" "BRNL" "BRC" 13
		       "A747",  // 2130 "A747" "BRNE" "BRC" 13
		       "A742",  // 2140 "A742" "BRP" "BRC" 13
		       "A744",  // 2150 "A744" "BRM" "BRC" 13
		       "A748",  // 2160 "A748" "BRZ" "BRC" 13
		       "A741",  // 2170 "A741" "BRO" "BRC" 13
		       "A74D",  // 2180 "A74D" "BRNP" "BRC" 13
		       "A74B",  // 2190 "A74B" "BRNM" "BRC" 13
		       "A747",  // 2200 "A747" "BRNZ" "BRC" 13
		       "A74E",  // 2210 "A74E" "BRNO" "BRC" 13
		       "A742",  // 2220 "A742" "JH" "BRC" 13
		       "A744",  // 2230 "A744" "JL" "BRC" 13
		       "A748",  // 2240 "A748" "JE" "BRC" 13
		       "A74D",  // 2250 "A74D" "JNH" "BRC" 13
		       "A74B",  // 2260 "A74B" "JNL" "BRC" 13
		       "A747",  // 2270 "A747" "JNE" "BRC" 13
		       "A742",  // 2280 "A742" "JP" "BRC" 13
		       "A744",  // 2290 "A744" "JM" "BRC" 13
		       "A748",  // 2300 "A748" "JZ" "BRC" 13
		       "A741",  // 2310 "A741" "JO" "BRC" 13
		       "A74D",  // 2320 "A74D" "JNP" "BRC" 13
		       "A74B",  // 2330 "A74B" "JNM" "BRC" 13
		       "A747",  // 2340 "A747" "JNZ" "BRC" 13
		       "A74E",  // 2350 "A74E" "JNO" "BRC" 13
		       "A75",  // 2360 "A75" "BRAS" "RI" 12
		       "A75",  // 2370 "A75" "JAS" "RI" 12
		       "A76",  // 2380 "A76" "BRCT" "RI" 12
		       "A76",  // 2390 "A76" "JCT" "RI" 12
		       "A77",  // 2400 "A77" "BRCTG" "RI" 12
		       "A77",  // 2410 "A77" "JCTG" "RI" 12
		       "A78",  // 2420 "A78" "LHI" "RI" 12
		       "A79",  // 2430 "A79" "LGHI" "RI" 12
		       "A7A",  // 2440 "A7A" "AHI" "RI" 12
		       "A7B",  // 2450 "A7B" "AGHI" "RI" 12
		       "A7C",  // 2460 "A7C" "MHI" "RI" 12
		       "A7D",  // 2470 "A7D" "MGHI" "RI" 12
		       "A7E",  // 2480 "A7E" "CHI" "RI" 12
		       "A7F",  // 2490 "A7F" "CGHI" "RI" 12
		       "A8",  // 2500 "A8" "MVCLE" "RS" 10
		       "A9",  // 2510 "A9" "CLCLE" "RS" 10
		       "AC",  // 2520 "AC" "STNSM" "SI" 11
		       "AD",  // 2530 "AD" "STOSM" "SI" 11
		       "AE",  // 2540 "AE" "SIGP" "RS" 10
		       "AF",  // 2550 "AF" "MC" "SI" 11
		       "B1",  // 2560 "B1" "LRA" "RX" 5
		       "B202",  // 2570 "B202" "STIDP" "S" 7
		       "B204",  // 2580 "B204" "SCK" "S" 7
		       "B205",  // 2590 "B205" "STCK" "S" 7
		       "B206",  // 2600 "B206" "SCKC" "S" 7
		       "B207",  // 2610 "B207" "STCKC" "S" 7
		       "B208",  // 2620 "B208" "SPT" "S" 7
		       "B209",  // 2630 "B209" "STPT" "S" 7
		       "B20A",  // 2640 "B20A" "SPKA" "S" 7
		       "B20B",  // 2650 "B20B" "IPK" "S" 7
		       "B20D",  // 2660 "B20D" "PTLB" "S" 7
		       "B210",  // 2670 "B210" "SPX" "S" 7
		       "B211",  // 2680 "B211" "STPX" "S" 7
		       "B212",  // 2690 "B212" "STAP" "S" 7
		       "B218",  // 2700 "B218" "PC" "S" 7
		       "B219",  // 2710 "B219" "SAC" "S" 7
		       "B21A",  // 2720 "B21A" "CFC" "S" 7
		       "B221",  // 2730 "B221" "IPTE" "RRE" 14
		       "B222",  // 2740 "B222" "IPM" "RRE" 14
		       "B223",  // 2750 "B223" "IVSK" "RRE" 14
		       "B224",  // 2760 "B224" "IAC" "RRE" 14
		       "B225",  // 2770 "B225" "SSAR" "RRE" 14
		       "B226",  // 2780 "B226" "EPAR" "RRE" 14
		       "B227",  // 2790 "B227" "ESAR" "RRE" 14
		       "B228",  // 2800 "B228" "PT" "RRE" 14
		       "B229",  // 2810 "B229" "ISKE" "RRE" 14
		       "B22A",  // 2820 "B22A" "RRBE" "RRE" 14
		       "B22B",  // 2830 "B22B" "SSKE" "RRE" 14
		       "B22C",  // 2840 "B22C" "TB" "RRE" 14
		       "B22D",  // 2850 "B22D" "DXR" "RRE" 14
		       "B22E",  // 2860 "B22E" "PGIN" "RRE" 14
		       "B22F",  // 2870 "B22F" "PGOUT" "RRE" 14
		       "B230",  // 2880 "B230" "CSCH" "S" 7
		       "B231",  // 2890 "B231" "HSCH" "S" 7
		       "B232",  // 2900 "B232" "MSCH" "S" 7
		       "B233",  // 2910 "B233" "SSCH" "S" 7
		       "B234",  // 2920 "B234" "STSCH" "S" 7
		       "B235",  // 2930 "B235" "TSCH" "S" 7
		       "B236",  // 2940 "B236" "TPI" "S" 7
		       "B237",  // 2950 "B237" "SAL" "S" 7
		       "B238",  // 2960 "B238" "RSCH" "S" 7
		       "B239",  // 2970 "B239" "STCRW" "S" 7
		       "B23A",  // 2980 "B23A" "STCPS" "S" 7
		       "B23B",  // 2990 "B23B" "RCHP" "S" 7
		       "B23C",  // 3000 "B23C" "SCHM" "S" 7
		       "B240",  // 3010 "B240" "BAKR" "RRE" 14
		       "B241",  // 3020 "B241" "CKSM" "RRE" 14
		       "B244",  // 3030 "B244" "SQDR" "RRE" 14
		       "B245",  // 3040 "B245" "SQER" "RRE" 14
		       "B246",  // 3050 "B246" "STURA" "RRE" 14
		       "B247",  // 3060 "B247" "MSTA" "RRE" 14
		       "B248",  // 3070 "B248" "PALB" "RRE" 14
		       "B249",  // 3080 "B249" "EREG" "RRE" 14
		       "B24A",  // 3090 "B24A" "ESTA" "RRE" 14
		       "B24B",  // 3100 "B24B" "LURA" "RRE" 14
		       "B24C",  // 3110 "B24C" "TAR" "RRE" 14
		       "B24D",  // 3120 "B24D" "CPYA" "RRE" 14
		       "B24E",  // 3130 "B24E" "SAR" "RRE" 14
		       "B24F",  // 3140 "B24F" "EAR" "RRE" 14
		       "B250",  // 3150 "B250" "CSP" "RRE" 14
		       "B252",  // 3160 "B252" "MSR" "RRE" 14
		       "B254",  // 3170 "B254" "MVPG" "RRE" 14
		       "B255",  // 3180 "B255" "MVST" "RRE" 14
		       "B257",  // 3190 "B257" "CUSE" "RRE" 14
		       "B258",  // 3200 "B258" "BSG" "RRE" 14
		       "B25A",  // 3210 "B25A" "BSA" "RRE" 14
		       "B25D",  // 3220 "B25D" "CLST" "RRE" 14
		       "B25E",  // 3230 "B25E" "SRST" "RRE" 14
		       "B263",  // 3240 "B263" "CMPSC" "RRE" 14
		       "B276",  // 3250 "B276" "XSCH" "S" 7
		       "B277",  // 3260 "B277" "RP" "S" 7
		       "B278",  // 3270 "B278" "STCKE" "S" 7
		       "B279",  // 3280 "B279" "SACF" "S" 7
		       "B27C",  //      "B27C" "STCKF" "S" 7 Z9-2
		       "B27D",  // 3290 "B27D" "STSI" "S" 7
		       "B299",  // 3300 "B299" "SRNM" "S" 7
		       "B29C",  // 3310 "B29C" "STFPC" "S" 7
		       "B29D",  // 3320 "B29D" "LFPC" "S" 7
		       "B2A5",  // 3330 "B2A5" "TRE" "RRE" 14
		       "B2A6",  // 3340 "B2A6" "CUUTF" "RRE" 14
		       "B2A6",  // 3350 "B2A6" "CU21" "RRE" 14
		       "B2A7",  // 3360 "B2A7" "CUTFU" "RRE" 14
		       "B2A7",  // 3370 "B2A7" "CU12" "RRE" 14
		       "B2B0",  //      "B2B0" "STFLE" "S" 7 Z9-3
		       "B2B1",  // 3380 "B2B1" "STFL" "S" 7
		       "B2B2",  // 3390 "B2B2" "LPSWE" "S" 7
		       "B2FF",  // 3400 "B2FF" "TRAP4" "S" 7
		       "B300",  // 3410 "B300" "LPEBR" "RRE" 14
		       "B301",  // 3420 "B301" "LNEBR" "RRE" 14
		       "B302",  // 3430 "B302" "LTEBR" "RRE" 14
		       "B303",  // 3440 "B303" "LCEBR" "RRE" 14
		       "B304",  // 3450 "B304" "LDEBR" "RRE" 14
		       "B305",  // 3460 "B305" "LXDBR" "RRE" 14
		       "B306",  // 3470 "B306" "LXEBR" "RRE" 14
		       "B307",  // 3480 "B307" "MXDBR" "RRE" 14
		       "B308",  // 3490 "B308" "KEBR" "RRE" 14
		       "B309",  // 3500 "B309" "CEBR" "RRE" 14
		       "B30A",  // 3510 "B30A" "AEBR" "RRE" 14
		       "B30B",  // 3520 "B30B" "SEBR" "RRE" 14
		       "B30C",  // 3530 "B30C" "MDEBR" "RRE" 14
		       "B30D",  // 3540 "B30D" "DEBR" "RRE" 14
		       "B30E",  // 3550 "B30E" "MAEBR" "RRF1" 15
		       "B30F",  // 3560 "B30F" "MSEBR" "RRF1" 15
		       "B310",  // 3570 "B310" "LPDBR" "RRE" 14
		       "B311",  // 3580 "B311" "LNDBR" "RRE" 14
		       "B312",  // 3590 "B312" "LTDBR" "RRE" 14
		       "B313",  // 3600 "B313" "LCDBR" "RRE" 14
		       "B314",  // 3610 "B314" "SQEBR" "RRE" 14
		       "B315",  // 3620 "B315" "SQDBR" "RRE" 14
		       "B316",  // 3630 "B316" "SQXBR" "RRE" 14
		       "B317",  // 3640 "B317" "MEEBR" "RRE" 14
		       "B318",  // 3650 "B318" "KDBR" "RRE" 14
		       "B319",  // 3660 "B319" "CDBR" "RRE" 14
		       "B31A",  // 3670 "B31A" "ADBR" "RRE" 14
		       "B31B",  // 3680 "B31B" "SDBR" "RRE" 14
		       "B31C",  // 3690 "B31C" "MDBR" "RRE" 14
		       "B31D",  // 3700 "B31D" "DDBR" "RRE" 14
		       "B31E",  // 3710 "B31E" "MADBR" "RRF1" 15
		       "B31F",  // 3720 "B31F" "MSDBR" "RRF1" 15
		       "B324",  // 3730 "B324" "LDER" "RRE" 14
		       "B325",  // 3740 "B325" "LXDR" "RRE" 14
		       "B326",  // 3750 "B326" "LXER" "RRE" 14
		       "B32E",  // 3760 "B32E" "MAER" "RRF1" 15
		       "B32F",  // 3770 "B32F" "MSER" "RRF1" 15
		       "B336",  // 3780 "B336" "SQXR" "RRE" 14
		       "B337",  // 3790 "B337" "MEER" "RRE" 14
		       "B338",  //      "B338" "MAYLR" "RRF1" 15 Z9-4
		       "B339",  //      "B339" "MYLR" "RRF1" 15 Z9-5
		       "B33A",  //      "B33A" "MAYR" "RRF1" 15 Z9-6
		       "B33B",  //      "B33B" "MYR" "RRF1" 15 Z9-7
		       "B33C",  //      "B33C" "MAYHR" "RRF1" 15 Z9-8
		       "B33D",  //      "B33D" "MYHR" "RRF1" 15 Z9-9
		       "B33E",  // 3800 "B33E" "MADR" "RRF1" 15
		       "B33F",  // 3810 "B33F" "MSDR" "RRF1" 15
		       "B340",  // 3820 "B340" "LPXBR" "RRE" 14
		       "B341",  // 3830 "B341" "LNXBR" "RRE" 14
		       "B342",  // 3840 "B342" "LTXBR" "RRE" 14
		       "B343",  // 3850 "B343" "LCXBR" "RRE" 14
		       "B344",  // 3860 "B344" "LEDBR" "RRE" 14
		       "B345",  // 3870 "B345" "LDXBR" "RRE" 14
		       "B346",  // 3880 "B346" "LEXBR" "RRE" 14
		       "B347",  // 3890 "B347" "FIXBR" "RRF2" 34
		       "B348",  // 3900 "B348" "KXBR" "RRE" 14
		       "B349",  // 3910 "B349" "CXBR" "RRE" 14
		       "B34A",  // 3920 "B34A" "AXBR" "RRE" 14
		       "B34B",  // 3930 "B34B" "SXBR" "RRE" 14
		       "B34C",  // 3940 "B34C" "MXBR" "RRE" 14
		       "B34D",  // 3950 "B34D" "DXBR" "RRE" 14
		       "B350",  // 3960 "B350" "TBEDR" "RRF2" 34
		       "B351",  // 3970 "B351" "TBDR" "RRF2" 34
		       "B353",  // 3980 "B353" "DIEBR" "RRF2" 34
		       "B357",  // 3990 "B357" "FIEBR" "RRF2" 34
		       "B358",  // 4000 "B358" "THDER" "RRE" 14
		       "B359",  // 4010 "B359" "THDR" "RRE" 14
		       "B35B",  // 4020 "B35B" "DIDBR" "RRF2" 34
		       "B35F",  // 4030 "B35F" "FIDBR" "RRF2" 34
		       "B360",  // 4040 "B360" "LPXR" "RRE" 14
		       "B361",  // 4050 "B361" "LNXR" "RRE" 14
		       "B362",  // 4060 "B362" "LTXR" "RRE" 14
		       "B363",  // 4070 "B363" "LCXR" "RRE" 14
		       "B365",  // 4080 "B365" "LXR" "RRE" 14
		       "B366",  // 4090 "B366" "LEXR" "RRE" 14
		       "B367",  // 4100 "B367" "FIXR" "RRE" 14
		       "B369",  // 4110 "B369" "CXR" "RRE" 14
		       "B374",  // 4120 "B374" "LZER" "RRE" 14
		       "B375",  // 4130 "B375" "LZDR" "RRE" 14
		       "B376",  // 4140 "B376" "LZXR" "RRE" 14
		       "B377",  // 4150 "B377" "FIER" "RRE" 14
		       "B37F",  // 4160 "B37F" "FIDR" "RRE" 14
		       "B384",  // 4170 "B384" "SFPC" "RRE" 14
		       "B38C",  // 4180 "B38C" "EFPC" "RRE" 14
		       "B394",  // 4190 "B394" "CEFBR" "RRE" 14
		       "B395",  // 4200 "B395" "CDFBR" "RRE" 14
		       "B396",  // 4210 "B396" "CXFBR" "RRE" 14
		       "B398",  // 4220 "B398" "CFEBR" "RRF2" 34
		       "B399",  // 4230 "B399" "CFDBR" "RRF2" 34
		       "B39A",  // 4240 "B39A" "CFXBR" "RRF2" 34
		       "B3A4",  // 4250 "B3A4" "CEGBR" "RRE" 14
		       "B3A5",  // 4260 "B3A5" "CDGBR" "RRE" 14
		       "B3A6",  // 4270 "B3A6" "CXGBR" "RRE" 14
		       "B3A8",  // 4280 "B3A8" "CGEBR" "RRF2" 34
		       "B3A9",  // 4290 "B3A9" "CGDBR" "RRF2" 34
		       "B3AA",  // 4300 "B3AA" "CGXBR" "RRF2" 34
		       "B3B4",  // 4310 "B3B4" "CEFR" "RRE" 14
		       "B3B5",  // 4320 "B3B5" "CDFR" "RRE" 14
		       "B3B6",  // 4330 "B3B6" "CXFR" "RRE" 14
		       "B3B8",  // 4340 "B3B8" "CFER" "RRF2" 34
		       "B3B9",  // 4350 "B3B9" "CFDR" "RRF2" 34
		       "B3BA",  // 4360 "B3BA" "CFXR" "RRF2" 34
		       "B3C4",  // 4370 "B3C4" "CEGR" "RRE" 14
		       "B3C5",  // 4380 "B3C5" "CDGR" "RRE" 14
		       "B3C6",  // 4390 "B3C6" "CXGR" "RRE" 14
		       "B3C8",  // 4400 "B3C8" "CGER" "RRF2" 34
		       "B3C9",  // 4410 "B3C9" "CGDR" "RRF2" 34
		       "B3CA",  // 4420 "B3CA" "CGXR" "RRF2" 34
		       "B6",  // 4430 "B6" "STCTL" "RS" 10
		       "B7",  // 4440 "B7" "LCTL" "RS" 10
		       "B900",  // 4450 "B900" "LPGR" "RRE" 14
		       "B901",  // 4460 "B901" "LNGR" "RRE" 14
		       "B902",  // 4470 "B902" "LTGR" "RRE" 14
		       "B903",  // 4480 "B903" "LCGR" "RRE" 14
		       "B904",  // 4490 "B904" "LGR" "RRE" 14
		       "B905",  // 4500 "B905" "LURAG" "RRE" 14
		       "B906",  //      "B906" "LGBR" "RRE" 14 Z9-10
		       "B907",  //      "B907" "LGHR" "RRE" 14 Z9-11
		       "B908",  // 4510 "B908" "AGR" "RRE" 14
		       "B909",  // 4520 "B909" "SGR" "RRE" 14
		       "B90A",  // 4530 "B90A" "ALGR" "RRE" 14
		       "B90B",  // 4540 "B90B" "SLGR" "RRE" 14
		       "B90C",  // 4550 "B90C" "MSGR" "RRE" 14
		       "B90D",  // 4560 "B90D" "DSGR" "RRE" 14
		       "B90E",  // 4570 "B90E" "EREGG" "RRE" 14
		       "B90F",  // 4580 "B90F" "LRVGR" "RRE" 14
		       "B910",  // 4590 "B910" "LPGFR" "RRE" 14
		       "B911",  // 4600 "B911" "LNGFR" "RRE" 14
		       "B912",  // 4610 "B912" "LTGFR" "RRE" 14
		       "B913",  // 4620 "B913" "LCGFR" "RRE" 14
		       "B914",  // 4630 "B914" "LGFR" "RRE" 14
		       "B916",  // 4640 "B916" "LLGFR" "RRE" 14
		       "B917",  // 4650 "B917" "LLGTR" "RRE" 14
		       "B918",  // 4660 "B918" "AGFR" "RRE" 14
		       "B919",  // 4670 "B919" "SGFR" "RRE" 14
		       "B91A",  // 4680 "B91A" "ALGFR" "RRE" 14
		       "B91B",  // 4690 "B91B" "SLGFR" "RRE" 14
		       "B91C",  // 4700 "B91C" "MSGFR" "RRE" 14
		       "B91D",  // 4710 "B91D" "DSGFR" "RRE" 14
		       "B91E",  // 4720 "B91E" "KMAC" "RRE" 14
		       "B91F",  // 4730 "B91F" "LRVR" "RRE" 14
		       "B920",  // 4740 "B920" "CGR" "RRE" 14
		       "B921",  // 4750 "B921" "CLGR" "RRE" 14
		       "B925",  // 4760 "B925" "STURG" "RRE" 14
		       "B926",  //      "B926" "LBR" "RRE" 14 Z9-12
		       "B927",  //      "B927" "LHR" "RRE" 14 Z9-13
		       "B92E",  // 4770 "B92E" "KM" "RRE" 14
		       "B92F",  // 4780 "B92F" "KMC" "RRE" 14
		       "B930",  // 4790 "B930" "CGFR" "RRE" 14
		       "B931",  // 4800 "B931" "CLGFR" "RRE" 14
		       "B93E",  // 4810 "B93E" "KIMD" "RRE" 14
		       "B93F",  // 4820 "B93F" "KLMD" "RRE" 14
		       "B946",  // 4830 "B946" "BCTGR" "RRE" 14
		       "B980",  // 4840 "B980" "NGR" "RRE" 14
		       "B981",  // 4850 "B981" "OGR" "RRE" 14
		       "B982",  // 4860 "B982" "XGR" "RRE" 14
		       "B983",  //      "B983" "FLOGR" "RRE" 14 Z9-14
		       "B984",  //      "B984" "LLGCR" "RRE" 14 Z9-15
		       "B985",  //      "B985" "LLGHR" "RRE" 14 Z9-16
		       "B986",  // 4870 "B986" "MLGR" "RRE" 14
		       "B987",  // 4880 "B987" "DLGR" "RRE" 14
		       "B988",  // 4890 "B988" "ALCGR" "RRE" 14
		       "B989",  // 4900 "B989" "SLBGR" "RRE" 14
		       "B98A",  // 4910 "B98A" "CSPG" "RRE" 14
		       "B98D",  // 4920 "B98D" "EPSW" "RRE" 14
		       "B98E",  // 4930 "B98E" "IDTE" "RRF2" 34
		       "B990",  // 4940 "B990" "TRTT" "RRE" 14
		       "B991",  // 4950 "B991" "TRTO" "RRE" 14
		       "B992",  // 4960 "B992" "TROT" "RRE" 14
		       "B993",  // 4970 "B993" "TROO" "RRE" 14
		       "B994",  //      "B994" "LLCR" "RRE" 14 Z9-17
		       "B995",  //      "B995" "LLHR" "RRE" 14 Z9-18
		       "B996",  // 4980 "B996" "MLR" "RRE" 14
		       "B997",  // 4990 "B997" "DLR" "RRE" 14
		       "B998",  // 5000 "B998" "ALCR" "RRE" 14
		       "B999",  // 5010 "B999" "SLBR" "RRE" 14
		       "B99A",  // 5020 "B99A" "EPAIR" "RRE" 14
		       "B99B",  // 5030 "B99B" "ESAIR" "RRE" 14
		       "B99D",  // 5040 "B99D" "ESEA" "RRE" 14
		       "B99E",  // 5050 "B99E" "PTI" "RRE" 14
		       "B99F",  // 5060 "B99F" "SSAIR" "RRE" 14
		       "B9AA",  //      "B9AA" "LPTEA" "RRE" 14 Z9-19
		       "B9B0",  // 5070 "B9B0" "CU14" "RRE" 14
		       "B9B1",  // 5080 "B9B1" "CU24" "RRE" 14
		       "B9B2",  // 5090 "B9B2" "CU41" "RRE" 14
		       "B9B3",  // 5100 "B9B3" "CU42" "RRE" 14
		       "B9BE",  // 5110 "B9BE" "SRSTU" "RRE" 14
		       "BA",  // 5120 "BA" "CS" "RS" 10
		       "BB",  // 5130 "BB" "CDS" "RS" 10
		       "BD",  // 5140 "BD" "CLM" "RS" 10
		       "BE",  // 5150 "BE" "STCM" "RS" 10
		       "BF",  // 5160 "BF" "ICM" "RS" 10
		       "C00",  // 5170 "C00" "LARL" "RIL" 16
		       "C01",  //      "C01" "LGFI" "RIL" 16 Z9-20
		       "C04",  // 5180 "C04" "BRCL" "RIL" 16
		       "C040",  // 5390 "C040" "JLNOP" "BLX" 33
		       "C041",  // 5400 "C041" "BROL" "BLX" 33
		       "C041",  // 5410 "C041" "JLO" "BLX" 33
		       "C042",  // 5420 "C042" "BRHL" "BLX" 33
		       "C042",  // 5430 "C042" "BRPL" "BLX" 33
		       "C042",  // 5440 "C042" "JLH" "BLX" 33
		       "C042",  // 5450 "C042" "JLP" "BLX" 33
		       "C044",  // 5460 "C044" "BRLL" "BLX" 33
		       "C044",  // 5470 "C044" "BRML" "BLX" 33
		       "C044",  // 5480 "C044" "JLL" "BLX" 33
		       "C044",  // 5490 "C044" "JLM" "BLX" 33
		       "C047",  // 5500 "C047" "BRNEL" "BLX" 33
		       "C047",  // 5510 "C047" "BRNZL" "BLX" 33
		       "C047",  // 5520 "C047" "JLNE" "BLX" 33
		       "C047",  // 5530 "C047" "JLNZ" "BLX" 33
		       "C048",  // 5540 "C048" "BREL" "BLX" 33
		       "C048",  // 5550 "C048" "BRZL" "BLX" 33
		       "C048",  // 5560 "C048" "JLE" "BLX" 33
		       "C048",  // 5570 "C048" "JLZ" "BLX" 33
		       "C04B",  // 5580 "C04B" "BRNLL" "BLX" 33
		       "C04B",  // 5590 "C04B" "BRNML" "BLX" 33
		       "C04B",  // 5600 "C04B" "JLNL" "BLX" 33
		       "C04B",  // 5610 "C04B" "JLNM" "BLX" 33
		       "C04D",  // 5620 "C04D" "BRNHL" "BLX" 33
		       "C04D",  // 5630 "C04D" "BRNPL" "BLX" 33
		       "C04D",  // 5640 "C04D" "JLNH" "BLX" 33
		       "C04D",  // 5650 "C04D" "JLNP" "BLX" 33
		       "C04E",  // 5660 "C04E" "BRNOL" "BLX" 33
		       "C04E",  // 5670 "C04E" "JLNO" "BLX" 33
		       "C04F",  // 5680 "C04F" "BRUL" "BLX" 33
		       "C04F",  // 5690 "C04F" "JLU" "BLX" 33
		       "C05",  // 5210 "C05" "BRASL" "RIL" 16
		       "C05",  // 5220 "C05" "JASL" "RIL" 16
		       "C06",  //      "C06" "XIHF" "RIL" 16 Z9-21
		       "C07",  //      "C07" "XILF" "RIL" 16 Z9-22
		       "C08",  //      "C08" "IIHF" "RIL" 16 Z9-23
		       "C09",  //      "C09" "IILF" "RIL" 16 Z9-24
		       "C0A",  //      "C0A" "NIHF" "RIL" 16 Z9-25
		       "C0B",  //      "C0B" "NILF" "RIL" 16 Z9-26
		       "C0C",  //      "C0C" "OIHF" "RIL" 16 Z9-27
		       "C0D",  //      "C0D" "OILF" "RIL" 16 Z9-28
		       "C0E",  //      "C0E" "LLIHF" "RIL" 16 Z9-29
		       "C0F",  //      "C0F" "LLILF" "RIL" 16 Z9-30
		       "C24",  //      "C24" "SLGFI" "RIL" 16 Z9-31
		       "C25",  //      "C25" "SLFI" "RIL" 16 Z9-32
		       "C28",  //      "C28" "AGFI" "RIL" 16 Z9-33
		       "C29",  //      "C29" "AFI" "RIL" 16 Z9-34
		       "C2A",  //      "C2A" "ALGFI" "RIL" 16 Z9-35
		       "C2B",  //      "C2B" "ALFI" "RIL" 16 Z9-36
		       "C2C",  //      "C2C" "CGFI" "RIL" 16 Z9-37
		       "C2D",  //      "C2D" "CFI" "RIL" 16 Z9-38
		       "C2E",  //      "C2E" "CLGFI" "RIL" 16 Z9-39
		       "C2F",  //      "C2F" "CLFI" "RIL" 16 Z9-40
		       "C80",  //      "C80" "MVCOS" "SSF" 32 Z9-41
		       "D0",  // 5230 "D0" "TRTR" "SS" 17
		       "D1",  // 5240 "D1" "MVN" "SS" 17
		       "D2",  // 5250 "D2" "MVC" "SS" 17
		       "D3",  // 5260 "D3" "MVZ" "SS" 17
		       "D4",  // 5270 "D4" "NC" "SS" 17
		       "D5",  // 5280 "D5" "CLC" "SS" 17
		       "D6",  // 5290 "D6" "OC" "SS" 17
		       "D7",  // 5300 "D7" "XC" "SS" 17
		       "D9",  // 5310 "D9" "MVCK" "SS" 17
		       "DA",  // 5320 "DA" "MVCP" "SS" 17
		       "DB",  // 5330 "DB" "MVCS" "SS" 17
		       "DC",  // 5340 "DC" "TR" "SS" 17
		       "DD",  // 5350 "DD" "TRT" "SS" 17
		       "DE",  // 5360 "DE" "ED" "SS" 17
		       "DF",  // 5370 "DF" "EDMK" "SS" 17
		       "E1",  // 5380 "E1" "PKU" "SS" 17
		       "E2",  // 5390 "E2" "UNPKU" "SS" 17
		       "E302",  //      "E302" "LTG" "RXY" 18 Z9-42
		       "E303",  // 5400 "E303" "LRAG" "RXY" 18
		       "E304",  // 5410 "E304" "LG" "RXY" 18
		       "E306",  // 5420 "E306" "CVBY" "RXY" 18
		       "E308",  // 5430 "E308" "AG" "RXY" 18
		       "E309",  // 5440 "E309" "SG" "RXY" 18
		       "E30A",  // 5450 "E30A" "ALG" "RXY" 18
		       "E30B",  // 5460 "E30B" "SLG" "RXY" 18
		       "E30C",  // 5470 "E30C" "MSG" "RXY" 18
		       "E30D",  // 5480 "E30D" "DSG" "RXY" 18
		       "E30E",  // 5490 "E30E" "CVBG" "RXY" 18
		       "E30F",  // 5500 "E30F" "LRVG" "RXY" 18
		       "E312",  //      "E312" "LT" "RXY" 18 Z9-43
		       "E313",  // 5510 "E313" "LRAY" "RXY" 18
		       "E314",  // 5520 "E314" "LGF" "RXY" 18
		       "E315",  // 5530 "E315" "LGH" "RXY" 18
		       "E316",  // 5540 "E316" "LLGF" "RXY" 18
		       "E317",  // 5550 "E317" "LLGT" "RXY" 18
		       "E318",  // 5560 "E318" "AGF" "RXY" 18
		       "E319",  // 5570 "E319" "SGF" "RXY" 18
		       "E31A",  // 5580 "E31A" "ALGF" "RXY" 18
		       "E31B",  // 5590 "E31B" "SLGF" "RXY" 18
		       "E31C",  // 5600 "E31C" "MSGF" "RXY" 18
		       "E31D",  // 5610 "E31D" "DSGF" "RXY" 18
		       "E31E",  // 5620 "E31E" "LRV" "RXY" 18
		       "E31F",  // 5630 "E31F" "LRVH" "RXY" 18
		       "E320",  // 5640 "E320" "CG" "RXY" 18
		       "E321",  // 5650 "E321" "CLG" "RXY" 18
		       "E324",  // 5660 "E324" "STG" "RXY" 18
		       "E326",  // 5670 "E326" "CVDY" "RXY" 18
		       "E32E",  // 5680 "E32E" "CVDG" "RXY" 18
		       "E32F",  // 5690 "E32F" "STRVG" "RXY" 18
		       "E330",  // 5700 "E330" "CGF" "RXY" 18
		       "E331",  // 5710 "E331" "CLGF" "RXY" 18
		       "E33E",  // 5720 "E33E" "STRV" "RXY" 18
		       "E33F",  // 5730 "E33F" "STRVH" "RXY" 18
		       "E346",  // 5740 "E346" "BCTG" "RXY" 18
		       "E350",  // 5750 "E350" "STY" "RXY" 18
		       "E351",  // 5760 "E351" "MSY" "RXY" 18
		       "E354",  // 5770 "E354" "NY" "RXY" 18
		       "E355",  // 5780 "E355" "CLY" "RXY" 18
		       "E356",  // 5790 "E356" "OY" "RXY" 18
		       "E357",  // 5800 "E357" "XY" "RXY" 18
		       "E358",  // 5810 "E358" "LY" "RXY" 18
		       "E359",  // 5820 "E359" "CY" "RXY" 18
		       "E35A",  // 5830 "E35A" "AY" "RXY" 18
		       "E35B",  // 5840 "E35B" "SY" "RXY" 18
		       "E35E",  // 5850 "E35E" "ALY" "RXY" 18
		       "E35F",  // 5860 "E35F" "SLY" "RXY" 18
		       "E370",  // 5870 "E370" "STHY" "RXY" 18
		       "E371",  // 5880 "E371" "LAY" "RXY" 18
		       "E372",  // 5890 "E372" "STCY" "RXY" 18
		       "E373",  // 5900 "E373" "ICY" "RXY" 18
		       "E376",  // 5910 "E376" "LB" "RXY" 18
		       "E377",  // 5920 "E377" "LGB" "RXY" 18
		       "E378",  // 5930 "E378" "LHY" "RXY" 18
		       "E379",  // 5940 "E379" "CHY" "RXY" 18
		       "E37A",  // 5950 "E37A" "AHY" "RXY" 18
		       "E37B",  // 5960 "E37B" "SHY" "RXY" 18
		       "E380",  // 5970 "E380" "NG" "RXY" 18
		       "E381",  // 5980 "E381" "OG" "RXY" 18
		       "E382",  // 5990 "E382" "XG" "RXY" 18
		       "E386",  // 6000 "E386" "MLG" "RXY" 18
		       "E387",  // 6010 "E387" "DLG" "RXY" 18
		       "E388",  // 6020 "E388" "ALCG" "RXY" 18
		       "E389",  // 6030 "E389" "SLBG" "RXY" 18
		       "E38E",  // 6040 "E38E" "STPQ" "RXY" 18
		       "E38F",  // 6050 "E38F" "LPQ" "RXY" 18
		       "E390",  // 6060 "E390" "LLGC" "RXY" 18
		       "E391",  // 6070 "E391" "LLGH" "RXY" 18
		       "E394",  //      "E394" "LLC" "RXY" 18 Z9-44
		       "E395",  //      "E395" "LLH" "RXY" 18 Z9-45
		       "E396",  // 6080 "E396" "ML" "RXY" 18
		       "E397",  // 6090 "E397" "DL" "RXY" 18
		       "E398",  // 6100 "E398" "ALC" "RXY" 18
		       "E399",  // 6110 "E399" "SLB" "RXY" 18
		       "E500",  // 6120 "E500" "LASP" "SSE" 19
		       "E501",  // 6130 "E501" "TPROT" "SSE" 19
		       "E502",  // 6140 "E502" "STRAG" "SSE" 19
		       "E50E",  // 6150 "E50E" "MVCSK" "SSE" 19
		       "E50F",  // 6160 "E50F" "MVCDK" "SSE" 19
		       "E8",  // 6170 "E8" "MVCIN" "SS" 17
		       "E9",  // 6180 "E9" "PKA" "SS" 31
		       "EA",  // 6190 "EA" "UNPKA" "SS" 17
		       "EB04",  // 6200 "EB04" "LMG" "RSY" 20
		       "EB0A",  // 6210 "EB0A" "SRAG" "RSY" 20
		       "EB0B",  // 6220 "EB0B" "SLAG" "RSY" 20
		       "EB0C",  // 6230 "EB0C" "SRLG" "RSY" 20
		       "EB0D",  // 6240 "EB0D" "SLLG" "RSY" 20
		       "EB0F",  // 6250 "EB0F" "TRACG" "RSY" 20
		       "EB14",  // 6260 "EB14" "CSY" "RSY" 20
		       "EB1C",  // 6270 "EB1C" "RLLG" "RSY" 20
		       "EB1D",  // 6280 "EB1D" "RLL" "RSY" 20
		       "EB20",  // 6290 "EB20" "CLMH" "RSY" 20
		       "EB21",  // 6300 "EB21" "CLMY" "RSY" 20
		       "EB24",  // 6310 "EB24" "STMG" "RSY" 20
		       "EB25",  // 6320 "EB25" "STCTG" "RSY" 20
		       "EB26",  // 6330 "EB26" "STMH" "RSY" 20
		       "EB2C",  // 6340 "EB2C" "STCMH" "RSY" 20
		       "EB2D",  // 6350 "EB2D" "STCMY" "RSY" 20
		       "EB2F",  // 6360 "EB2F" "LCTLG" "RSY" 20
		       "EB30",  // 6370 "EB30" "CSG" "RSY" 20
		       "EB31",  // 6380 "EB31" "CDSY" "RSY" 20
		       "EB3E",  // 6390 "EB3E" "CDSG" "RSY" 20
		       "EB44",  // 6400 "EB44" "BXHG" "RSY" 20
		       "EB45",  // 6410 "EB45" "BXLEG" "RSY" 20
		       "EB51",  // 6420 "EB51" "TMY" "SIY" 21
		       "EB52",  // 6430 "EB52" "MVIY" "SIY" 21
		       "EB54",  // 6440 "EB54" "NIY" "SIY" 21
		       "EB55",  // 6450 "EB55" "CLIY" "SIY" 21
		       "EB56",  // 6460 "EB56" "OIY" "SIY" 21
		       "EB57",  // 6470 "EB57" "XIY" "SIY" 21
		       "EB80",  // 6480 "EB80" "ICMH" "RSY" 20
		       "EB81",  // 6490 "EB81" "ICMY" "RSY" 20
		       "EB8E",  // 6500 "EB8E" "MVCLU" "RSY" 20
		       "EB8F",  // 6510 "EB8F" "CLCLU" "RSY" 20
		       "EB90",  // 6520 "EB90" "STMY" "RSY" 20
		       "EB96",  // 6530 "EB96" "LMH" "RSY" 20
		       "EB98",  // 6540 "EB98" "LMY" "RSY" 20
		       "EB9A",  // 6550 "EB9A" "LAMY" "RSY" 20
		       "EB9B",  // 6560 "EB9B" "STAMY" "RSY" 20
		       "EBC0",  // 6570 "EBC0" "TP" "RSL" 22
		       "EC44",  // 6580 "EC44" "BRXHG" "RIE" 23
		       "EC44",  // 6590 "EC44" "JXHG" "RIE" 23
		       "EC45",  // 6600 "EC45" "BRXLG" "RIE" 23
		       "EC45",  // 6610 "EC45" "JXLEG" "RIE" 23
		       "ED04",  // 6620 "ED04" "LDEB" "RXE" 24
		       "ED05",  // 6630 "ED05" "LXDB" "RXE" 24
		       "ED06",  // 6640 "ED06" "LXEB" "RXE" 24
		       "ED07",  // 6650 "ED07" "MXDB" "RXE" 24
		       "ED08",  // 6660 "ED08" "KEB" "RXE" 24
		       "ED09",  // 6670 "ED09" "CEB" "RXE" 24
		       "ED0A",  // 6680 "ED0A" "AEB" "RXE" 24
		       "ED0B",  // 6690 "ED0B" "SEB" "RXE" 24
		       "ED0C",  // 6700 "ED0C" "MDEB" "RXE" 24
		       "ED0D",  // 6710 "ED0D" "DEB" "RXE" 24
		       "ED0E",  // 6720 "ED0E" "MAEB" "RXF" 25
		       "ED0F",  // 6730 "ED0F" "MSEB" "RXF" 25
		       "ED10",  // 6740 "ED10" "TCEB" "RXE" 24
		       "ED11",  // 6750 "ED11" "TCDB" "RXE" 24
		       "ED12",  // 6760 "ED12" "TCXB" "RXE" 24
		       "ED14",  // 6770 "ED14" "SQEB" "RXE" 24
		       "ED15",  // 6780 "ED15" "SQDB" "RXE" 24
		       "ED17",  // 6790 "ED17" "MEEB" "RXE" 24
		       "ED18",  // 6800 "ED18" "KDB" "RXE" 24
		       "ED19",  // 6810 "ED19" "CDB" "RXE" 24
		       "ED1A",  // 6820 "ED1A" "ADB" "RXE" 24
		       "ED1B",  // 6830 "ED1B" "SDB" "RXE" 24
		       "ED1C",  // 6840 "ED1C" "MDB" "RXE" 24
		       "ED1D",  // 6850 "ED1D" "DDB" "RXE" 24
		       "ED1E",  // 6860 "ED1E" "MADB" "RXF" 25
		       "ED1F",  // 6870 "ED1F" "MSDB" "RXF" 25
		       "ED24",  // 6880 "ED24" "LDE" "RXE" 24
		       "ED25",  // 6890 "ED25" "LXD" "RXE" 24
		       "ED26",  // 6900 "ED26" "LXE" "RXE" 24
		       "ED2E",  // 6910 "ED2E" "MAE" "RXF" 25
		       "ED2F",  // 6920 "ED2F" "MSE" "RXF" 25
		       "ED34",  // 6930 "ED34" "SQE" "RXE" 24
		       "ED35",  // 6940 "ED35" "SQD" "RXE" 24
		       "ED37",  // 6950 "ED37" "MEE" "RXE" 24
		       "ED38",  //      "ED38" "MAYL" "RXF" 25 Z9-46
		       "ED39",  //      "ED39" "MYL" "RXF" 25 Z9-47
		       "ED3A",  //      "ED3A" "MAY" "RXF" 25 Z9-48
		       "EDEB",  //      "EDEB" "MY" "RXF" 25 Z9-49
		       "ED3C",  //      "ED3C" "MAYH" "RXF" 25 Z9-50
		       "EDED",  //      "EDED" "MYH" "RXF" 25 Z9-51
		       "ED3E",  // 6960 "ED3E" "MAD" "RXF" 25
		       "ED3F",  // 6970 "ED3F" "MSD" "RXF" 25
		       "ED64",  // 6980 "ED64" "LEY" "RXY" 18
		       "ED65",  // 6990 "ED65" "LDY" "RXY" 18
		       "ED66",  // 7000 "ED66" "STEY" "RXY" 18
		       "ED67",  // 7010 "ED67" "STDY" "RXY" 18
		       "EE",  // 7020 "EE" "PLO" "SS3" 27
		       "EF",  // 7030 "EF" "LMD" "SS4" 28
		       "F0",  // 7040 "F0" "SRP" "SS5" 29
		       "F1",  // 7050 "F1" "MVO" "SS2" 26
		       "F2",  // 7060 "F2" "PACK" "SS2" 26
		       "F3",  // 7070 "F3" "UNPK" "SS2" 26
		       "F8",  // 7080 "F8" "ZAP" "SS2" 26
		       "F9",  // 7090 "F9" "CP" "SS2" 26
		       "FA",  // 7100 "FA" "AP" "SS2" 26
		       "FB",  // 7110 "FB" "SP" "SS2" 26
		       "FC",  // 7120 "FC" "MP" "SS2" 26
		       "FD",  // 7130 "FD" "DP" "SS2" 26		       
			};
      /*
       * DS/DC type tables shared by mz390 and az390
       */
      String dc_valid_types   = "ABCDEFHLPSVXYZ";
      String dc_type_explicit = "RBCKKGGKPRVXRZ";
      int[] dc_type_len = {
      		4,  // A
			1,  // B
			1,  // C
			8,  // D
			4,  // E
			4,  // F
			2,  // H
			16, // L
			1,  // P
			2,  // S
			4,  // V
			1,  // X
			2,  // Y
			1   // Z
			};
      int[] dc_type_align = {
      		4,  // A
			0,  // B
			0,  // C
			8,  // D
			4,  // E
			4,  // F
			2,  // H
			8,  // L
			0,  // P
			2,  // S
			4,  // V
			0,  // X
			2,  // Y
			0   // Z
			};
      char[] dc_type_delimiter = {
      		'(',  // A
			'\'', // B
			'\'', // C
			'\'', // D
			'\'', // E
			'\'', // F
			'\'', // H
			'\'', // L
			'\'', // P
			'(',  // S
			'(',  // V
			'\'', // X
			'(',  // Y
			'\''  // Z
			};
      /*
       * key search table data
       */
      int last_key_op = 0;
      static int key_not_found = 1;
      static int key_found = 2;
      static int max_key_root = 4000;
      int max_key_tab = 50000;
      int tot_key_tab = max_key_root+1;
      int tot_key = 0;
      String key_text = null;
      int key_index = 0;
      int key_index_last = 0;
      Random key_rand = new Random();
      int key_hash = 0;
      int tot_key_search = 0;
      int tot_key_comp  = 0;
      int avg_key_comp  = 0;
      int cur_key_comp = 0;
      int max_key_comp = 0;
      String[]  key_tab_key   = new String[max_key_tab];
      int[]     key_tab_hash  = (int[])Array.newInstance(int.class,max_key_tab);
      int[]     key_tab_index = (int[])Array.newInstance(int.class,max_key_tab);
      int[]     key_tab_low   = (int[])Array.newInstance(int.class,max_key_tab);
      int[]     key_tab_high  = (int[])Array.newInstance(int.class,max_key_tab);
public void init_tables(){
	/*
	 * initialize dir_cur and tables
	 */
	set_dir_cur();  //RPI168
	init_ascii_ebcdic();
	if (op_name.length != op_type.length){
		abort_options("opcode tables out of sync - aborting");
	}
	int index = 0;
	int max_type = 0;
	int ins_count = 0;
	while (index < op_type.length){
		if (op_type[index] < 100){
			if (op_type[index] > max_type){
				max_type = op_type[index];
			}
			ins_count++;
		}
		index++;
	}
	if (max_type != max_op_type_offset){
		abort_options("opcode max type out of sync - " + max_type + " vs " + max_op_type_offset);
	}
	if (ins_count != op_code.length){
		abort_options("opcode total out of sync - aborting");
	}
}
public void init_options(String[] args,String pgm_type){
	/*
	 * parse and set options
	 * Notes:
	 *   1.  These use () vs = because bat removes =
	 *        syslog(ddname)
	 *        sys390(ddname)
	 *        test(ddname)
	 *        time(seconds)
	 */
    if  (args.length >= 1){
    	if (!set_pgm_dir_name_type(args[0],pgm_type)){
    		abort_options("invalid input file option - " + args[0]);
    	}
    	dir_390 = pgm_dir;
    	dir_bal = pgm_dir;
    	dir_cpy = pgm_dir;
        dir_dat = pgm_dir;
        dir_log = pgm_dir;
        dir_lst = pgm_dir;
        dir_mac = pgm_dir;
    	dir_mlc = pgm_dir;
    	dir_obj = pgm_dir;
    	dir_pch = pgm_dir;
    	dir_prn = pgm_dir;
        if (args.length > 1){
           cmd_parms = args[1];
           int index1 = 2;
           while (index1 < args.length){
            	cmd_parms = cmd_parms.concat(" " + args[index1]);
           	 	index1++;
           }
        }
    } else {
	    abort_options("missing file option");
    }
    String token = null;
    int index1 = 1;
    while (index1 < args.length){
    	token = args[index1];
    	if (token.length() > 2 //RPI201
    		&& token.charAt(0) == '"'
    		&& token.charAt(token.length()-1) == '"'){
    		token = token.substring(1,token.length()-1);
    	}
    	if (token.toUpperCase().equals("AMODE24")){
    		opt_amode24 = true;
    		opt_amode31 = false;
    		z390_amode31 = 'F';
    		z390_rmode31 = 'F';
    	} else if (token.toUpperCase().equals("AMODE31")){
    		opt_amode24 = false;
    		opt_amode31 = true;
    		z390_amode31 = 'T';
    	} else if (token.toUpperCase().equals("ASCII")){
    		opt_ascii = true;        		
    	} else if (token.toUpperCase().equals("CON")){
           	opt_con = true;
        } else if (token.toUpperCase().equals("DUMP")){
           	opt_dump = true;
        } else if (token.length() > 4
        	&& token.substring(0,4).toUpperCase().equals("ERR(")){
           	try {
           		max_errors = Integer.valueOf(token.substring(4,token.length()-1)).intValue();
          	} catch (Exception e){
           		abort_options("invalid error limit - " + token);
           	}
        } else if (token.toUpperCase().equals("GUAM")){
           	opt_guam = true;
        } else if (token.length() > 8
        	&& token.substring(0,8).toUpperCase().equals("MAXFILE(")){
           	try {
           		max_file = Long.valueOf(token.substring(8,token.length()-1)).longValue() << 20;;
           	} catch (Exception e){
           		abort_options("invalid maxfile limit (mb) - " + token);
           	}
        } else if (token.length() > 5
        	&& token.substring(0,4).toUpperCase().equals("MEM(")){
           	try {
           	    max_mem = Integer.valueOf(token.substring(4,token.length()-1)).intValue();
           	} catch (Exception e){
           		abort_options("invalid memory option " + token);
           	}
        } else if (token.toUpperCase().equals("NOCON")){
           	opt_con = false;
        } else if (token.toUpperCase().equals("NOLIST")){
           	opt_list = false;
        } else if (token.toUpperCase().equals("NOLISTCALL")){
           	opt_listcall = false;
        } else if (token.equals("NOLISTFILE")){
           	opt_listfile = false;
         } else if (token.toUpperCase().equals("NOMFC")){
           	opt_mfc = false;
         } else if (token.toUpperCase().equals("NOSTATS")){
           	opt_stats = false;
         } else if (token.toUpperCase().equals("NOTIME")){
          	opt_time = false; // no time limit
         } else if (token.toUpperCase().equals("NOTIMING")){
          	opt_timing = false; // no date/time changes
          	opt_time   = false;
         } else if (token.toUpperCase().equals("NOTRAP")){
           	opt_trap = false;
         } else if (token.toUpperCase().equals("NOXREF")){
           	opt_xref = false;
         } else if (token.toUpperCase().equals("OBJHEX")){
           	opt_objhex = true;
         } else if (token.length() > 5
           		&& token.substring(0,5).toUpperCase().equals("PARM(")){
            	opt_parm = token.substring(5,token.length()-1);
            	if (opt_parm.length() > 2 
            		&& opt_parm.charAt(0) == '\''
            		&& opt_parm.charAt(opt_parm.length()-1) == '\''){
            		opt_parm = opt_parm.substring(1,opt_parm.length()-1);          		
            	}
         } else if (token.toUpperCase().equals("REGS")){
           	opt_regs = true;
           	opt_list  = true;
         } else if (token.toUpperCase().equals("RMODE24")){
           	opt_rmode24 = true;
           	opt_rmode31 = false;
           	z390_rmode31 = 'F';
         } else if (token.toUpperCase().equals("RMODE31")){
           	opt_rmode24 = false;
          	opt_rmode31 = true;
           	z390_rmode31 = 'T';
         } else if (token.length() > 7
           		&& token.substring(0,7).toUpperCase().equals("SYS390(")){
           	dir_390 = token.substring(7,token.length()-1) + File.separator;	
         } else if (token.length() > 7 
           		&& token.substring(0,7).toUpperCase().equals("SYSBAL(")){
          	dir_bal = token.substring(7,token.length()-1) + File.separator; 
         } else if (token.length() > 7 
          		&& token.substring(0,7).toUpperCase().equals("SYSCPY(")){
           	dir_cpy = token.substring(7,token.length()-1); 
         } else if (token.length() > 7 
          		&& token.substring(0,7).toUpperCase().equals("SYSDAT(")){
           	dir_dat = token.substring(7,token.length()-1) + File.separator; 
         } else if (token.length() > 7
          		&& token.substring(0,7).toUpperCase().equals("SYSLOG(")){
           	dir_log = token.substring(7,token.length()-1) + File.separator;
         } else if (token.length() > 7 
           		&& token.substring(0,7).toUpperCase().equals("SYSMAC(")){
           	dir_mac = token.substring(7,token.length()-1); 
         } else if (token.length() > 7 
           		&& token.substring(0,7).toUpperCase().equals("SYSMLC(")){
          	dir_mlc = get_short_file_name(token.substring(7,token.length()-1) + File.separator); 
         } else if (token.length() > 7 
           		&& token.substring(0,7).toUpperCase().equals("SYSOBJ(")){
           	dir_obj = token.substring(7,token.length()-1) + File.separator; 
         } else if (token.length() > 8
         		&& token.substring(0,8).toUpperCase().equals("SYSPARM(")){
        	opt_sysparm = token.substring(8,token.length()-1);
         } else if (token.length() > 7 
           		&& token.substring(0,7).toUpperCase().equals("SYSPCH(")){
          	dir_pch = get_short_file_name(token.substring(7,token.length()-1) + File.separator); 
         } else if (token.length() > 7 
          		&& token.substring(0,7).toUpperCase().equals("SYSPRN(")){
          	dir_prn = token.substring(7,token.length()-1) + File.separator; 	
         } else if (token.length() > 5
          		&& token.substring(0,5).toUpperCase().equals("TIME(")){
           	max_time_seconds = Long.valueOf(token.substring(5,token.length()-1)).longValue();
         } else if (token.toUpperCase().equals("TEST")){
           	opt_test = true;
           	opt_time = false;
         } else if (token.length() > 5
          		&& token.substring(0,5).toUpperCase().equals("TEST(")){
           	test_ddname = token.substring(5,token.length()-1);	
           	opt_test = true;
         } else if (token.toUpperCase().equals("TRACE")){
           	opt_trace = true;
           	opt_list   = true;
           	opt_con   = false;
         } else if (token.toUpperCase().equals("TRACEA")){
           	opt_tracea = true;
           	opt_list = true;	
         } else if (token.toUpperCase().equals("TRACEALL")){
           	opt_traceall = true;
           	opt_trace    = true;
           	opt_tracem   = true;
           	opt_tracea   = true;
           	opt_tracel   = true;
           	opt_tracemem = true;
           	opt_list     = true;
           	opt_con   = false;
         } else if (token.toUpperCase().equals("TRACEL")){
           	opt_tracel = true;
         } else if (token.toUpperCase().equals("TRACEMEM")){
           	opt_tracemem = true;
         }
         index1++;
    }
}
private String get_short_file_name(String file_name){
	/*
	 * return shortest file name possible
	 * with quotes if LSN
	 */
	if (file_name.length() > dir_cur.length()
		&& file_name.substring(0,dir_cur.length()).equals(dir_cur)){
		if (file_name.substring(dir_cur.length(),dir_cur.length()+1).equals(File.separator)){
			file_name = file_name.substring(dir_cur.length()+1); // skip dir + sep
		} else {
			file_name = file_name.substring(dir_cur.length()); // skip dir
		}
	}
	int index = file_name.indexOf(" ");
	if (index >=0){
		return "\"" + file_name + "\""; // LSN
	}
	return file_name;
}
private void abort_options(String msg){
	/*
	 * display options error on system out
	 * and exit with rc 16.
	 */
	System.out.println("TZ390E set options error - " + msg);
	System.exit(16);
}

private void init_ascii_ebcdic(){
	/*
	 * init ascii/ebcdic conversion tables
	 */	
    int index = 0;
	while (index < 256){
	  ascii_to_ebcdic[index] = (byte) Integer.valueOf(ascii_to_ebcdic_hex.substring(index*2,index*2+2),16).intValue();
	  ebcdic_to_ascii[index] = (byte) Integer.valueOf(ebcdic_to_ascii_hex.substring(index*2,index*2+2),16).intValue();
	  index++;
	}
}
public int find_key_index(String user_key){
	/*
	 * return user_key_index for user_key else -1
	 * and set following for possible add_key_index:
	 *    1.  key_text = user_key
	 *    2.  key_hash = hash code for key
	 *    3.  key_index_last = last search entry
	 * Notes:
	 *   1.  Usage my mz390
	 *       a.  "F:" - macro and copybook files
	 *       b.  "G:" - global set variables
	 *       c.  "K:" - macro keyword parm names
	 *       d.  "L:" - local set variable
	 *       e.  "M:" - loaded macros
	 *       f.  "O:" - opcode table (init_opcode_name_keys)
	 *       g.  "P:" - macro positional parm names
	 *       h.  "S:" - ordinary symbols
	 *       i.  "X:" - executable macro command
	 *   2.  Usage by az390
	 *       a.  "L:" - literals
	 *       b.  "O:" - opcode table (init_opcode_name_keys)
	 *       c.  "S:" - ordinary symbols
	 *       d.  "U:" - USING labels
	 *   3.  Usage by lz390
	 *       a.  "G:" - global ESD's
	 *   4.  Usage by ez390
	 *       a.  "H:" - opcodes by hex key
	 *       b.  "H:BR:" - branch opocodes by hex key
	 *       c.  "O:" - opcodes by name (init_opcode_name_keys)
	 *       d.  "P:" - CDE program name lookup
	 */
	tot_key_search++;
	key_text = user_key;
    key_rand.setSeed((long) key_text.hashCode());
    key_hash  = key_rand.nextInt();
    key_index = key_rand.nextInt(max_key_root)+1;
	if (key_tab_key[key_index] == null){
		key_index_last = key_index;
		last_key_op = key_not_found;
		return -1;
	}
    cur_key_comp = 0;
	while (key_index > 0){ 
		tot_key_comp++;
		cur_key_comp++;
		if (cur_key_comp > max_key_comp){
			max_key_comp = cur_key_comp;
		}
		if (key_hash == key_tab_hash[key_index]
		    && user_key.equals(key_tab_key[key_index])){
			last_key_op = key_found;
	    	return key_tab_index[key_index];
	    }
		key_index_last = key_index;
		if (key_hash < key_tab_hash[key_index]){
		    key_index = key_tab_low[key_index];
		} else {
			key_index = key_tab_high[key_index];
		}
	}
	last_key_op = key_not_found;
	return -1;
}
public boolean add_key_index(int user_index){
	/*
	 * add user_index entry based on
	 * key_text, key_hash, and key_index_last
	 * set by prior find_key_index
	 * 
	 */
	if (last_key_op != key_not_found){
		return false;
	}
	if (key_tab_key[key_index_last] == null){
		key_index = key_index_last;
	} else {
		if (tot_key_tab < max_key_tab){
			key_index = tot_key_tab;
			tot_key_tab++;
		} else {
			return false;
		}
		if (key_hash < key_tab_hash[key_index_last]){
		    key_tab_low[key_index_last] = key_index;
	    } else {
		    key_tab_high[key_index_last] = key_index;
	    }
	}
	tot_key++;
	key_tab_key[key_index]   = key_text;
	key_tab_hash[key_index]  = key_hash;
	key_tab_index[key_index] = user_index;
	return true;
}
public boolean update_key_index(int user_key){
	/*
	 * update previously found key index
	 */
	if (last_key_op != key_found){
		return false;
	}
	key_tab_index[key_index] = user_key;
	return true;
}
public void reset_op_name_index(){
	/*
	 * reset op_code table index if changed
	 * by opsyn during previous pass.
	 */
	int index = 0;
	while (index < op_name.length){
		if (find_key_index("O:" + op_name[index]) != -1){
			update_key_index(index); 
		}
		index++;
	}
}
public String get_file_name(String parm_dir,String parm,String parm_type){
	   /*
	    * 1.  Strip long spacey name quotes if found
	    * 2.  Add directory and type if not specified
	    */
	   	    String file_name = null;
	    	if (parm.charAt(0) == '\"' 
	    		|| parm.charAt(0) == '\''){
	    		file_name = parm.substring(1,parm.length() - 1);
	    	} else {
	    	    file_name = parm;
	    	}
	    	if  (file_name.length() > 0
	            && file_name.indexOf('\\') == -1
	    		&& file_name.indexOf(':')  == -1){
	    		file_name = parm_dir.concat(File.separator + file_name);	
	    	}
	    	int index = file_name.indexOf(".");
	    	if (index == -1){
	    		file_name = file_name.trim() + parm_type;
	    	}
	    	return file_name;
}
public String find_file_name(String parm_dir_list, String file_name, String file_type_def, String dir_cur){
	/*
	 * search for existing file in one or more dirs
	 * and return file name or null if not found
	 * Note:
	 *   1.  The separator for multiple files may be ; or +
	 *       (plus sign) verus semi-colon is used in BAT parms 
	 *       to avoid conflict with Windows BAT parsing.
	 *   2.  If file_name has type use it.
	 *       Else if directory path has *.type use
	 *       the type instead of default file_type. 
	 */
	String file_dir;
	String file_type;
	String temp_file_name;
	File   temp_file;
	int index = file_name.indexOf('.');
	boolean file_type_set = false;
	if (index > 0){
		file_type_set = true;
	}
	index = 0;
	int path_len = 0;  
	while (index <= parm_dir_list.length()){
		file_type = file_type_def;
		int index1 = parm_dir_list.substring(index).indexOf(";");
		if (index1 == -1)index1 = parm_dir_list.substring(index).indexOf("+");
		if (index1 > 0){
			path_len = path_len + index1;   
			file_dir = parm_dir_list.substring(index,path_len); // RPI123
			index = index + index1 + 1;
			path_len = path_len + 1;    
		} else {
			file_dir = parm_dir_list.substring(index);
			index = parm_dir_list.length()+1;
		}
		if (file_dir.equals(".")){
			file_dir = dir_cur;
		}
		int index2 = file_dir.indexOf("*.");
		if (index2 > 0){
			file_type = file_dir.substring(index2+1);
			file_dir  = file_dir.substring(0,index2);
		}
		if (file_dir.length() > 0){
			if (!file_dir.substring(file_dir.length()-1,file_dir.length()).equals(File.separator)){
				file_dir = file_dir + File.separator;
			}
			if (file_type_set){
				temp_file_name = file_dir + file_name;
			} else {
				temp_file_name = file_dir + file_name + file_type;
			}
			temp_file = new File(temp_file_name);
		} else {
			temp_file = new File(file_name + file_type);
		}
		if (temp_file.isFile()){
			return temp_file.getPath().toUpperCase();
		}
	}
	return null;
}
public boolean exec_cmd(String cmd){
     /*
      * exec command as separate task
      */
           try {
  	           Runtime.getRuntime().exec(cmd);
  	           return true;
  	       } catch(Exception e){
  	   	       return false;
  	       }
  	  }
public boolean init_opcode_name_keys(){
	/*
	 * add all opcodes to key index table
	 */
	int index = 0;
	while (index < op_name.length){
		if (find_key_index("O:" + op_name[index]) == -1){
			if(!add_key_index(index)){ 
				return false;
			}
		} else {
			return false;
		}
		index++;
	}
	return true;
}
public boolean set_pgm_dir_name_type(String file_name,String file_type){
	/*
	 * set pgm_dir, pgm_name, pgm_type from parm 
	 * Notes:
	 *   1.  Only allow file type override for MLC.
	 */
	set_dir_cur(); //RPI168
	if (file_name.charAt(0) == '\"'   // strip lsn quotes
		|| file_name.charAt(0) == '\''){
		file_name = file_name.substring(1,file_name.length() - 1);
	}
    int index = file_name.lastIndexOf(File.separator);
    if (index != -1){  // get dir path if any
    	pgm_dir = file_name.substring(0,index+1);
    	file_name = file_name.substring(index + 1).toUpperCase();
    } else if (file_name.length() > 1 && file_name.charAt(1) == ':'){
    	File temp_file = new File(file_name.substring(0,2));
    	try {
    		pgm_dir = temp_file.getCanonicalPath() + File.separator;
    	} catch (Exception e){
    		return false;
    	}
    	file_name = file_name.substring(2); //RPI113
    } else {
    	pgm_dir = dir_cur;
	  	file_name = file_name.toUpperCase();
    }
    index = file_name.lastIndexOf('.');
    if (index != -1){  // strip extension if any
    	pgm_name = file_name.substring(0,index);
    	if (!file_type.equals(".MLC")){ //RPI169
    		pgm_type=file_type;
    	} else {
    		pgm_type = file_name.substring(index);
    	}
    } else {
     	pgm_name = file_name;
     	pgm_type = file_type;
    }
    return true;
}
private void set_dir_cur(){  //RPI168
	/*
	 * set current directory dir_cur
	 */
	dir_cur = System.getProperty("user.dir").toUpperCase() + File.separator;
}
public boolean opsyn_opcode_update(String new_op,String old_op){
	/*
	 * update O: opcode index table defining
	 * new_op as old_op
	 */
	int new_index = -1;
	int old_index = -1;
	if (old_op != null && old_op.length() > 0){
		old_index = find_key_index("O:" + old_op.toUpperCase());
	}
	if (new_op != null && new_op.length() > 0){
		new_index = find_key_index("O:" + new_op.toUpperCase());
	} else {
		return false; // no new op to change
	}
	if (new_index > 0){
		update_key_index(old_index); // replace opcode
		opsyn_name_change = true;
		return true;
	} else {
		if (add_key_index(old_index)){ // add new opcode
			return true;
		} else {
			return false;  // add failed
		}
	}
}
public String get_hex(int work_int,int field_length) {
   	/*
   	 * Format int into 1-16 byte hex string
   	 */
   	    String work_hex = Integer.toHexString(work_int);
   	    if (work_hex.length() >= field_length){
   	    	return work_hex.substring(work_hex.length() - field_length).toUpperCase();
   	    } else {
   			return ("0000000000000000" + work_hex).substring(16 - field_length + work_hex.length()).toUpperCase();
   	    }
   }
public boolean get_sdt_char_int(String sdt){
	   /*
	    *  set sdt_char_int to
	    *  value of character string else false
	    *  
	    *  C'....' EBCDIC/ASCII (rep ''|&& with'|&)
	    *  C"...." ASCII        (rep ""|''|&& with "|'|&)
	    *  C!....! EBCDIC       (rep !!|''|&& with !|'|&) 
	    */
	   int index = 2;
	   sdt_char_int = 0;
	   char sdt_quote = sdt.charAt(1);
	   while (index < sdt.length()-1){
		   if (sdt.charAt(index) == sdt_quote
				|| sdt.charAt(index) == '\''
				|| sdt.charAt(index) == '&'){
			   if (index + 1 < sdt.length()-1){
				   if (sdt.charAt(index+1) == sdt.charAt(index)){
					   index++;
				   } else {
					   return false;
				   }
			   }
		   }
		   if ((opt_ascii && sdt_quote == '\'') //RPI73 
				|| sdt_quote == '"'){           //RPI5
			   sdt_char_int = (sdt_char_int << 8) + (sdt.charAt(index) & 0xff);
		   } else {
			   sdt_char_int = (sdt_char_int << 8) + (ascii_to_ebcdic[sdt.charAt(index)] & 0xff);
		   }
		   if (index+1 == sdt.length()
			   && sdt.charAt(index) != sdt_quote){
			   return false;
		   }
	       index++;
	   }
	   return true;
}
}