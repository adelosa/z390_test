#ifndef IJBKVAL_H                                                               
#define IJBKVAL_H                                                               
                                                                                
#ifdef PROLOGUE                                                                 
 /**************************************************************                
 *                                                             *                
 *   LICENSED MATERIALS - PROPERTY OF IBM                      *                
 *   "RESTRICTED MATERIALS OF IBM"                             *                
 *   5686-066                                                  *                
 *   (C) COPYRIGHT IBM CORP. 1997, 1997                        *                
 *                                                             *                
 ***************************************************************                
 *                                                                              
 * SOURCE FILE NAME:  IJBKVAL.H                                                 
 *                                                                              
 * FILE DESCRIPTION:  Include file for key verification APIs                    
 *                                                                              
 * AUTHOR:  Ingo Adlung                                                         
 *                                                                              
 * NOTES:                                                                       
 *      PORTABILITY:                                                            
 *              VSE                                                             
 *                                                                              
 *      DEPENDENCIES:                                                           
 *              None                                                            
 *                                                                              
 *      RESTRICTIONS:                                                           
 *              None                                                            
 *                                                                              
 * Change History:                                                              
 *                                                                              
 */                                                                             
#endif                                                                          
                                                                                
                                                                                
     /* Return Codes from VSE Key validation API function */                    
                                                                                
#define  VSEKVRC_START 0                                                        
                                                                                
#define  VSEKVRC_SUCCESS                   0+VSEKVRC_START                      
#define  VSEKVRC_IMPORTKEY                 1+VSEKVRC_START                      
#define  VSEKVRC_ENCRYPTFAILED             2+VSEKVRC_START                      
                                                                                
#define  VSEKVRC_EXETYPEINVALID            3+VSEKVRC_START                      
#define  VSEKVRC_INVALIDENCRYPTTYPE        4+VSEKVRC_START                      
#define  VSEKVRC_FILENOTFOUND              5+VSEKVRC_START                      
#define  VSEKVRC_FILETOOSMALL              6+VSEKVRC_START                      
#define  VSEKVRC_BADKEYFILENAMELEN         7+VSEKVRC_START                      
#define  VSEKVRC_NOKEYFILE                 8+VSEKVRC_START                      
#define  VSEKVRC_KEYFILEEXIST              9+VSEKVRC_START                      
#define  VSEKVRC_FILEWRITEERROR           10+VSEKVRC_START                      
#define  VSEKVRC_FILEREADERROR            11+VSEKVRC_START                      
#define  VSEKVRC_INVALIDMACHID            12+VSEKVRC_START                      
#define  VSEKVRC_KEYDISKCORRUPT           13+VSEKVRC_START                      
#define  VSEKVRC_KEYFILECORRUPT           14+VSEKVRC_START                      
#define  VSEKVRC_DISKFORMATERROR          15+VSEKVRC_START                      
#define  VSEKVRC_DISKWRITEERROR           16+VSEKVRC_START                      
#define  VSEKVRC_DISKREADERROR            17+VSEKVRC_START                      
#define  VSEKVRC_NOTTBKEYFILE             18+VSEKVRC_START                      
#define  VSEKVRC_TBKEYALREADYUSED         19+VSEKVRC_START                      
#define  VSEKVRC_TRIESEXPIRED             20+VSEKVRC_START                      
#define  VSEKVRC_DAYSEXPIRED              21+VSEKVRC_START                      
#define  VSEKVRC_DATEEXPIRED              22+VSEKVRC_START                      
                                                                                
#define  VSEKVRC_BADCUSTOMERNUMBERLEN     23+VSEKVRC_START                      
#define  VSEKVRC_BADCUSTOMERNUMBERFRMT    24+VSEKVRC_START                      
#define  VSEKVRC_BADCUSTOMERNUMBERCHARS   25+VSEKVRC_START                      
                                                                                
#define  VSEKVRC_BADMACHINEIDLEN          26+VSEKVRC_START                      
#define  VSEKVRC_BADMACHINEIDFRMT         27+VSEKVRC_START                      
#define  VSEKVRC_BADMACHINEIDCHARS        28+VSEKVRC_START                      
#define  VSEKVRC_BADMACHINEIDCHECK        29+VSEKVRC_START                      
                                                                                
#define  VSEKVRC_BADLICENSEKEYLEN         30+VSEKVRC_START                      
#define  VSEKVRC_BADLICENSEKEYFRMT        31+VSEKVRC_START                      
#define  VSEKVRC_BADLICENSEKEYCHARS       32+VSEKVRC_START                      
#define  VSEKVRC_BADLICENSEKEYCHECK       33+VSEKVRC_START                      
                                                                                
#define  VSEKVRC_INVALIDKEYTYPE           34+VSEKVRC_START                      
#define  VSEKVRC_UNKNOWNKEYTYPE           35+VSEKVRC_START                      
#define  VSEKVRC_MISMATCHKEYTYPES         36+VSEKVRC_START                      
                                                                                
#define  VSEKVRC_BADENCRYPTEDDATALEN      37+VSEKVRC_START                      
#define  VSEKVRC_DECRYPTFAILED            38+VSEKVRC_START                      
                                                                                
#define  VSEKVRC_SDCSMASTERFILEIOERROR    39+VSEKVRC_START                      
#define  VSEKVRC_SDCSNOTREADY             40+VSEKVRC_START                      
#define  VSEKVRC_DATAKEYGENERATEFAILED    41+VSEKVRC_START                      
#define  VSEKVRC_INVALIDVERSION           42+VSEKVRC_START                      
#define  VSEKVRC_INVALIDMACHINEID         43+VSEKVRC_START                      
#define  VSEKVRC_WRONGKEYFILE             44+VSEKVRC_START                      
#define  VSEKVRC_CANNOTEXPORTIMPORT       45+VSEKVRC_START                      
#define  VSEKVRC_CANNOTEXPORTTYPE         46+VSEKVRC_START                      
#define  VSEKVRC_DISKHASAKEYFILE          47+VSEKVRC_START                      
#define  VSEKVRC_DESTFILEEXIST            48+VSEKVRC_START                      
#define  VSEKVRC_DRIVEDOESNOTEXIST        49+VSEKVRC_START                      
#define  VSEKVRC_ALREADYATTACHED          50+VSEKVRC_START                      
#define  VSEKVRC_INCORRECTDRIVETYPE       51+VSEKVRC_START                      
#define  VSEKVRC_DRIVENOTAVAILABLE        52+VSEKVRC_START                      
#define  VSEKVRC_INSUFFICIENTDISKSPACE    53+VSEKVRC_START                      
#define  VSEKVRC_SDCSMASTERFILEMEMERROR   54+VSEKVRC_START                      
#define  VSEKVRC_INVALIDOPTION            55+VSEKVRC_START                      
                                                                                
#define  VSEKVRC_TBISSUEPREMATURE         56+VSEKVRC_START                      
#define  VSEKVRC_TBISSUEEXPIRED           57+VSEKVRC_START                      
#define  VSEKVRC_TRYANDBUYRUNNING         58+VSEKVRC_START                      
#define  VSEKVRC_CANNOTRUNTRYANDBUY01     59+VSEKVRC_START                      
#define  VSEKVRC_CANNOTRUNTRYANDBUY02     60+VSEKVRC_START                      
#define  VSEKVRC_CANNOTRUNTRYANDBUY03     61+VSEKVRC_START                      
#define  VSEKVRC_CANNOTRUNTRYANDBUY04     62+VSEKVRC_START                      
                                                                                
                                                                                
#define  VSEKVRC_SIDEFILENOTFOUND         71+VSEKVRC_START                      
#define  VSEKVRC_SIDEFILEREADERROR        72+VSEKVRC_START                      
#define  VSEKVRC_INVALIDSIDEFILE          73+VSEKVRC_START                      
#define  VSEKVRC_DESTDATERESETFAILED      74+VSEKVRC_START                      
#define  VSEKVRC_DESTATTRIBRESETFAILED    75+VSEKVRC_START                      
                                                                                
                                                                                
     /* API calls */                                                            
                                                                                
#pragma linkage( VSEloadKeyExit, OS)                                            
#pragma map( VSEloadKeyExit, "IJBLKEYV")                                        
int VSEloadKeyExit( void);                                                      
                                                                                
#pragma linkage( VSEreleaseKeyExit, OS)                                         
#pragma map( VSEreleaseKeyExit, "IJBRKEYV")                                     
int VSEreleaseKeyExit( void);                                                   
                                                                                
#pragma linkage( VSEverifyKeyT0, OS)                                            
#pragma map( VSEverifyKeyT0, "IJBCKVT0")                                        
int VSEverifyKeyT0( char *pClearData,                                           
                    char *pEncryptedData,                                       
                    char *pCustNum,                                             
                    char *pLicenseKey );                                        
                                                                                
#endif /* IJBKVAL_H */                                                          
