package com.example.msplug.background_service;
import android.os.Bundle;
import java.util.List;
import android.telephony.NeighboringCellInfo;
import android.telephony.CellInfo;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.os.ParcelFileDescriptor;
import android.os.ResultReceiver;
import android.os.WorkSource;
import android.net.Uri;
import android.service.carrier.CarrierIdentifier;
import android.telecom.PhoneAccount;
import android.telecom.PhoneAccountHandle;
import android.telephony.CellIdentity;
import android.telephony.IccOpenLogicalChannelResponse;
import android.telephony.RadioAccessSpecifier;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.VisualVoicemailSmsFilterSettings;
import java.util.Map;

import android.telephony.UiccCardInfo;
import android.telephony.emergency.EmergencyNumber;

/**
 * Interface used to interact with the phone.  Mostly this is used by the
 * TelephonyManager class.  A few places are still using this directly.
 * Please clean them up if possible and use TelephonyManager instead.
 *
 * {@hide}
 */
interface ITelephony {

    /**
     * Dial a number. This doesn't place the call. It displays
     * the Dialer screen.
     * @param number the number to be dialed. If null, this
     * would display the Dialer screen with no number pre-filled.
     */
    void dial(String number);

    /**
     * Place a call to the specified number.
     * @param callingPackage The package making the call.
     * @param number the number to be called.
     */
    void call(String callingPackage, String number);

    /** @deprecated Use {@link #isRadioOnWithFeature(String, String) instead */
    boolean isRadioOn(String callingPackage);

    /**
     * Check to see if the radio is on or not.
     * @param callingPackage the name of the package making the call.
     * @param callingFeatureId The feature in the package.
     * @return returns true if the radio is on.
     */
    boolean isRadioOnWithFeature(String callingPackage, String callingFeatureId);

    /**
     * @deprecated Use {@link #isRadioOnForSubscriberWithFeature(int, String, String) instead
     */
    boolean isRadioOnForSubscriber(int subId, String callingPackage);

    /**
     * Check to see if the radio is on or not on particular subId.
     * @param subId user preferred subId.
     * @param callingPackage the name of the package making the call.
     * @param callingFeatureId The feature in the package.
     * @return returns true if the radio is on.
     */
    boolean isRadioOnForSubscriberWithFeature(int subId, String callingPackage, String callingFeatureId);


    /**
     * Supply a pin to unlock the SIM for particular subId.
     * Blocks until a result is determined.
     * @param pin The pin to check.
     * @param subId user preferred subId.
     * @return whether the operation was a success.
     */
    boolean supplyPinForSubscriber(int subId, String pin);

    /**
     * Supply puk to unlock the SIM and set SIM pin to new pin.
     *  Blocks until a result is determined.
     * @param puk The puk to check.
     *        pin The new pin to be set in SIM
     * @param subId user preferred subId.
     * @return whether the operation was a success.
     */
    boolean supplyPukForSubscriber(int subId, String puk, String pin);

    /**
     * Supply a pin to unlock the SIM.  Blocks until a result is determined.
     * Returns a specific success/error code.
     * @param pin The pin to check.
     * @return retValue[0] = Phone.PIN_RESULT_SUCCESS on success. Otherwise error code
     *         retValue[1] = number of attempts remaining if known otherwise -1
     */
    int[] supplyPinReportResultForSubscriber(int subId, String pin);

    /**
     * Supply puk to unlock the SIM and set SIM pin to new pin.
     * Blocks until a result is determined.
     * Returns a specific success/error code
     * @param puk The puk to check
     *        pin The pin to check.
     * @return retValue[0] = Phone.PIN_RESULT_SUCCESS on success. Otherwise error code
     *         retValue[1] = number of attempts remaining if known otherwise -1
     */
    int[] supplyPukReportResultForSubscriber(int subId, String puk, String pin);

    /**
     * Handles PIN MMI commands (PIN/PIN2/PUK/PUK2), which are initiated
     * without SEND (so <code>dial</code> is not appropriate).
     *
     * @param dialString the MMI command to be executed.
     * @return true if MMI command is executed.
     */
    boolean handlePinMmi(String dialString);


    /**
     * Handles USSD commands.
     *
     * @param subId The subscription to use.
     * @param ussdRequest the USSD command to be executed.
     */
    void handleUssdRequest(int subId, String ussdRequest, ResultReceiver wrappedCallback);

    /**
     * Handles PIN MMI commands (PIN/PIN2/PUK/PUK2), which are initiated
     * without SEND (so <code>dial</code> is not appropriate) for
     * a particular subId.
     * @param dialString the MMI command to be executed.
     * @param subId user preferred subId.
     * @return true if MMI command is executed.
     */
    boolean handlePinMmiForSubscriber(int subId, String dialString);

    /**
     * Toggles the radio on or off.
     */
    void toggleRadioOnOff();

    /**
     * Toggles the radio on or off on particular subId.
     * @param subId user preferred subId.
     */
    void toggleRadioOnOffForSubscriber(int subId);

    /**
     * Set the radio to on or off
     */
    boolean setRadio(boolean turnOn);

    /**
     * Set the radio to on or off on particular subId.
     * @param subId user preferred subId.
     */
    boolean setRadioForSubscriber(int subId, boolean turnOn);

    /**
     * Set the radio to on or off unconditionally
     */
    boolean setRadioPower(boolean turnOn);

    /**
     * This method has been removed due to security and stability issues.
     */
    void updateServiceLocation();

    /**
     * Version of updateServiceLocation that records the caller and validates permissions.
     */
    void updateServiceLocationWithPackageName(String callingPkg);

    /**
     * This method has been removed due to security and stability issues.
     */
    void enableLocationUpdates();

    /**
     * This method has been removed due to security and stability issues.
     */
    void disableLocationUpdates();

    /**
     * Allow mobile data connections.
     */
    boolean enableDataConnectivity();

    /**
     * Disallow mobile data connections.
     */
    boolean disableDataConnectivity();

    /**
     * Report whether data connectivity is possible.
     */
    boolean isDataConnectivityPossible(int subId);

    // Uses CellIdentity which is Parcelable here; will convert to CellLocation in client.
    CellIdentity getCellLocation(String callingPkg, String callingFeatureId);

    /**
     * Returns the ISO country code equivalent of the current registered
     * operator's MCC (Mobile Country Code).
     * @see android.telephony.TelephonyManager#getNetworkCountryIso
     */
    String getNetworkCountryIsoForPhone(int phoneId);

    /**
     * Returns the neighboring cell information of the device.
     */
    List<NeighboringCellInfo> getNeighboringCellInfo(String callingPkg, String callingFeatureId);

    int getCallState();

    /**
     * Returns the call state for a slot.
     */
    int getCallStateForSlot(int slotIndex);

    /**
     * Replaced by getDataActivityForSubId.
     */
    int getDataActivity();

    /**
     * Returns a constant indicating the type of activity on a data connection
     * (cellular).
     *
     */
    int getDataActivityForSubId(int subId);

    /**
     * Replaced by getDataStateForSubId.
     */
    int getDataState();

    /**
     * Returns a constant indicating the current data connection state
     * (cellular).
     *
     */
    int getDataStateForSubId(int subId);

    /**
     * Returns the current active phone type as integer.
     * Returns TelephonyManager.PHONE_TYPE_CDMA if RILConstants.CDMA_PHONE
     * and TelephonyManager.PHONE_TYPE_GSM if RILConstants.GSM_PHONE
     */
    int getActivePhoneType();

    /**
     * Returns the current active phone type as integer for particular slot.
     * Returns TelephonyManager.PHONE_TYPE_CDMA if RILConstants.CDMA_PHONE
     * and TelephonyManager.PHONE_TYPE_GSM if RILConstants.GSM_PHONE
     * @param slotIndex - slot to query.
     */
    int getActivePhoneTypeForSlot(int slotIndex);

    /**
     * Returns the CDMA ERI icon index to display
     * @param callingPackage package making the call.
     * @param callingFeatureId The feature in the package.
     */
    int getCdmaEriIconIndex(String callingPackage, String callingFeatureId);

    /**
     * Returns the CDMA ERI icon index to display on particular subId.
     * @param subId user preferred subId.
     * @param callingPackage package making the call.
     * @param callingFeatureId The feature in the package.
     */
    int getCdmaEriIconIndexForSubscriber(int subId, String callingPackage,
                                         String callingFeatureId);

    /**
     * Returns the CDMA ERI icon mode,
     * 0 - ON
     * 1 - FLASHING
     * @param callingPackage package making the call.
     * @param callingFeatureId The feature in the package.
     */
    int getCdmaEriIconMode(String callingPackage, String callingFeatureId);

    /**
     * Returns the CDMA ERI icon mode on particular subId,
     * 0 - ON
     * 1 - FLASHING
     * @param subId user preferred subId.
     * @param callingPackage package making the call.
     * @param callingFeatureId The feature in the package.
     */
    int getCdmaEriIconModeForSubscriber(int subId, String callingPackage,
                                        String callingFeatureId);

    /**
     * Returns the CDMA ERI text,
     * @param callingPackage package making the call.
     * @param callingFeatureId The feature in the package.
     */
    String getCdmaEriText(String callingPackage, String callingFeatureId);

    /**
     * Returns the CDMA ERI text for particular subId,
     * @param subId user preferred subId.
     * @param callingPackage package making the call.
     * @param callingFeatureId The feature in the package.
     */
    String getCdmaEriTextForSubscriber(int subId, String callingPackage, String callingFeatureId);

    /**
     * Returns true if OTA service provisioning needs to run.
     * Only relevant on some technologies, others will always
     * return false.
     */
    boolean needsOtaServiceProvisioning();

    /**
     * Sets the voicemail number for a particular subscriber.
     */
    boolean setVoiceMailNumber(int subId, String alphaTag, String number);

    /**
     * Sets the voice activation state for a particular subscriber.
     */
    void setVoiceActivationState(int subId, int activationState);

    /**
     * Sets the data activation state for a particular subscriber.
     */
    void setDataActivationState(int subId, int activationState);

    /**
     * Returns the voice activation state for a particular subscriber.
     * @param subId user preferred sub
     * @param callingPackage package queries voice activation state
     */
    int getVoiceActivationState(int subId, String callingPackage);

    /**
     * Returns the data activation state for a particular subscriber.
     * @param subId user preferred sub
     * @param callingPackage package queris data activation state
     */
    int getDataActivationState(int subId, String callingPackage);

    /**
     * Returns the unread count of voicemails for a subId.
     * @param subId user preferred subId.
     * Returns the unread count of voicemails
     */
    int getVoiceMessageCountForSubscriber(int subId, String callingPackage,
                                          String callingFeatureId);

    /**
     * Returns true if current state supports both voice and data
     * simultaneously. This can change based on location or network condition.
     */
    boolean isConcurrentVoiceAndDataAllowed(int subId);

    Bundle getVisualVoicemailSettings(String callingPackage, int subId);

    String getVisualVoicemailPackageName(String callingPackage, String callingFeatureId, int subId);

    // Not oneway, caller needs to make sure the vaule is set before receiving a SMS

    // Get settings set by the calling package
    VisualVoicemailSmsFilterSettings getVisualVoicemailSmsFilterSettings(String callingPackage,
                                                                         int subId);

    /**
     *  Get settings set by the current default dialer, Internal use only.
     *  Requires READ_PRIVILEGED_PHONE_STATE permission.
     */
    VisualVoicemailSmsFilterSettings getActiveVisualVoicemailSmsFilterSettings(int subId);

    /**
     * Send a visual voicemail SMS. Internal use only.
     * Requires caller to be the default dialer and have SEND_SMS permission
     */

    // Send the special dialer code. The IPC caller must be the current default dialer.
    void sendDialerSpecialCode(String callingPackageName, String inputCode);

    /**
     * Returns the network type of a subId.
     * @param subId user preferred subId.
     * @param callingPackage package making the call.
     * @param callingFeatureId The feature in the package.
     */
    int getNetworkTypeForSubscriber(int subId, String callingPackage, String callingFeatureId);

    /**
     * Returns the network type for data transmission
     * @param callingPackage package making the call.
     * @param callingFeatureId The feature in the package.
     */
    int getDataNetworkType(String callingPackage, String callingFeatureId);

    /**
     * Returns the data network type of a subId
     * @param subId user preferred subId.
     * @param callingPackage package making the call.
     * @param callingFeatureId The feature in the package.
     */
    int getDataNetworkTypeForSubscriber(int subId, String callingPackage,
                                        String callingFeatureId);

    /**
     * Returns the voice network type of a subId
     * @param subId user preferred subId.
     * @param callingPackage package making the call.getLteOnCdmaMode
     * @param callingFeatureId The feature in the package.
     * Returns the network type
     */
    int getVoiceNetworkTypeForSubscriber(int subId, String callingPackage,
                                         String callingFeatureId);

    /**
     * Return true if an ICC card is present
     */
    boolean hasIccCard();

    /**
     * Return true if an ICC card is present for a subId.
     * @param slotIndex user preferred slotIndex.
     * Return true if an ICC card is present
     */
    boolean hasIccCardUsingSlotIndex(int slotIndex);

    /**
     * Return if the current radio is LTE on CDMA. This
     * is a tri-state return value as for a period of time
     * the mode may be unknown.
     *
     * @param callingPackage the name of the calling package
     */
    int getLteOnCdmaMode(String callingPackage, String callingFeatureId);

    /**
     * Return if the current radio is LTE on CDMA. This
     * is a tri-state return value as for a period of time
     * the mode may be unknown.
     *
     * @param callingPackage the name of the calling package
     * @param callingFeatureId The feature in the package.
     */
    int getLteOnCdmaModeForSubscriber(int subId, String callingPackage, String callingFeatureId);

    /**
     * Returns all observed cell information of the device.
     */
    List<CellInfo> getAllCellInfo(String callingPkg, String callingFeatureId);

    /**
     * Request a cell information update for the specified subscription,
     * reported via the CellInfoCallback.
     */

    /**
     * Request a cell information update for the specified subscription,
     * reported via the CellInfoCallback.
     *
     */

    /**
     * Sets minimum time in milli-seconds between onCellInfoChanged
     */
    void setCellInfoListRate(int rateInMillis);

    /**
     * Opens a logical channel to the ICC card using the physical slot index.
     *
     * Input parameters equivalent to TS 27.007 AT+CCHO command.
     *
     * @param slotIndex The physical slot index of the target ICC card
     * @param callingPackage the name of the package making the call.
     * @param AID Application id. See ETSI 102.221 and 101.220.
     * @param p2 P2 parameter (described in ISO 7816-4).
     * @return an IccOpenLogicalChannelResponse object.
     */
    IccOpenLogicalChannelResponse iccOpenLogicalChannelBySlot(
            int slotIndex, String callingPackage, String AID, int p2);

    /**
     * Opens a logical channel to the ICC card.
     *
     * Input parameters equivalent to TS 27.007 AT+CCHO command.
     *
     * @param subId The subscription to use.
     * @param callingPackage the name of the package making the call.
     * @param AID Application id. See ETSI 102.221 and 101.220.
     * @param p2 P2 parameter (described in ISO 7816-4).
     * @return an IccOpenLogicalChannelResponse object.
     */
    IccOpenLogicalChannelResponse iccOpenLogicalChannel(
            int subId, String callingPackage, String AID, int p2);

    /**
     * Closes a previously opened logical channel to the ICC card using the physical slot index.
     *
     * Input parameters equivalent to TS 27.007 AT+CCHC command.
     *
     * @param slotIndex The physical slot index of the target ICC card
     * @param channel is the channel id to be closed as returned by a
     *            successful iccOpenLogicalChannel.
     * @return true if the channel was closed successfully.
     */
    boolean iccCloseLogicalChannelBySlot(int slotIndex, int channel);

    /**
     * Closes a previously opened logical channel to the ICC card.
     *
     * Input parameters equivalent to TS 27.007 AT+CCHC command.
     *
     * @param subId The subscription to use.
     * @param channel is the channel id to be closed as returned by a
     *            successful iccOpenLogicalChannel.
     * @return true if the channel was closed successfully.
     */
    boolean iccCloseLogicalChannel(int subId, int channel);

    /**
     * Transmit an APDU to the ICC card over a logical channel using the physical slot index.
     *
     * Input parameters equivalent to TS 27.007 AT+CGLA command.
     *
     * @param slotIndex The physical slot index of the target ICC card
     * @param channel is the channel id to be closed as returned by a
     *            successful iccOpenLogicalChannel.
     * @param cla Class of the APDU command.
     * @param instruction Instruction of the APDU command.
     * @param p1 P1 value of the APDU command.
     * @param p2 P2 value of the APDU command.
     * @param p3 P3 value of the APDU command. If p3 is negative a 4 byte APDU
     *            is sent to the SIM.
     * @param data Data to be sent with the APDU.
     * @return The APDU response from the ICC card with the status appended at
     *            the end.
     */
    String iccTransmitApduLogicalChannelBySlot(int slotIndex, int channel, int cla, int instruction,
                                               int p1, int p2, int p3, String data);

    /**
     * Transmit an APDU to the ICC card over a logical channel.
     *
     * Input parameters equivalent to TS 27.007 AT+CGLA command.
     *
     * @param subId The subscription to use.
     * @param channel is the channel id to be closed as returned by a
     *            successful iccOpenLogicalChannel.
     * @param cla Class of the APDU command.
     * @param instruction Instruction of the APDU command.
     * @param p1 P1 value of the APDU command.
     * @param p2 P2 value of the APDU command.
     * @param p3 P3 value of the APDU command. If p3 is negative a 4 byte APDU
     *            is sent to the SIM.
     * @param data Data to be sent with the APDU.
     * @return The APDU response from the ICC card with the status appended at
     *            the end.
     */
    String iccTransmitApduLogicalChannel(int subId, int channel, int cla, int instruction,
                                         int p1, int p2, int p3, String data);

    /**
     * Transmit an APDU to the ICC card over the basic channel using the physical slot index.
     *
     * Input parameters equivalent to TS 27.007 AT+CSIM command.
     *
     * @param slotIndex The physical slot index of the target ICC card
     * @param callingPackage the name of the package making the call.
     * @param cla Class of the APDU command.
     * @param instruction Instruction of the APDU command.
     * @param p1 P1 value of the APDU command.
     * @param p2 P2 value of the APDU command.
     * @param p3 P3 value of the APDU command. If p3 is negative a 4 byte APDU
     *            is sent to the SIM.
     * @param data Data to be sent with the APDU.
     * @return The APDU response from the ICC card with the status appended at
     *            the end.
     */
    String iccTransmitApduBasicChannelBySlot(int slotIndex, String callingPackage, int cla,
                                             int instruction, int p1, int p2, int p3, String data);

    /**
     * Transmit an APDU to the ICC card over the basic channel.
     *
     * Input parameters equivalent to TS 27.007 AT+CSIM command.
     *
     * @param subId The subscription to use.
     * @param callingPackage the name of the package making the call.
     * @param cla Class of the APDU command.
     * @param instruction Instruction of the APDU command.
     * @param p1 P1 value of the APDU command.
     * @param p2 P2 value of the APDU command.
     * @param p3 P3 value of the APDU command. If p3 is negative a 4 byte APDU
     *            is sent to the SIM.
     * @param data Data to be sent with the APDU.
     * @return The APDU response from the ICC card with the status appended at
     *            the end.
     */
    String iccTransmitApduBasicChannel(int subId, String callingPackage, int cla, int instruction,
                                       int p1, int p2, int p3, String data);

    /**
     * Returns the response APDU for a command APDU sent through SIM_IO.
     *
     * @param subId The subscription to use.
     * @param fileID
     * @param command
     * @param p1 P1 value of the APDU command.
     * @param p2 P2 value of the APDU command.
     * @param p3 P3 value of the APDU command.
     * @param filePath
     * @return The APDU response.
     */
    byte[] iccExchangeSimIO(int subId, int fileID, int command, int p1, int p2, int p3,
                            String filePath);

    /**
     * Send ENVELOPE to the SIM and returns the response.
     *
     * @param subId The subscription to use.
     * @return The APDU response from the ICC card, with the last 4 bytes
     *         being the status word. If the command fails, returns an empty
     *         string.
     */
    String sendEnvelopeWithStatus(int subId, String content);

    /**
     * Used for device configuration by some CDMA operators.
     *
     * @param itemID the ID of the item to read.
     * @return the NV item as a String, or null on any failure.
     */
    String nvReadItem(int itemID);

    /**
     * Used for device configuration by some CDMA operators.
     *
     * @param itemID the ID of the item to read.
     * @param itemValue the value to write, as a String.
     * @return true on success; false on any failure.
     */
    boolean nvWriteItem(int itemID, String itemValue);

    /**
     * Update the CDMA Preferred Roaming List (PRL) in the radio NV storage.
     * Used for device configuration by some CDMA operators.
     *
     * @return true on success; false on any failure.
     */

    /**
     * Rollback modem configurations to factory default except some config which are in whitelist.
     * Used for device configuration by some CDMA operators.
     *
     * <p>Requires Permission:
     * {@link android.Manifest.permission#MODIFY_PHONE_STATE MODIFY_PHONE_STATE} or that the calling
     *
     * @param slotIndex - device slot.
     * @return {@code true} on success; {@code false} on any failure.
     */
    boolean resetModemConfig(int slotIndex);

    /**
     * Generate a radio modem reset. Used for device configuration by some CDMA operators.
     * Different than {@link #setRadioPower(boolean)}, modem reboot will power down sim card.
     *
     * <p>Requires Permission:
     * {@link android.Manifest.permission#MODIFY_PHONE_STATE MODIFY_PHONE_STATE} or that the calling
     *
     * @param slotIndex - device slot.
     * @return {@code true} on success; {@code false} on any failure.
     */
    boolean rebootModem(int slotIndex);
    /*
     * Get the calculated preferred network type.
     * Used for device configuration by some CDMA operators.
     * @param callingPackage The package making the call.
     * @param callingFeatureId The feature in the package.
     *
     * @return the calculated preferred network type, defined in RILConstants.java.
     */
    int getCalculatedPreferredNetworkType(String callingPackage, String callingFeatureId);

    /*
     * Get the preferred network type.
     * Used for device configuration by some CDMA operators.
     *
     * @param subId the id of the subscription to query.
     * @return the preferred network type, defined in RILConstants.java.
     */
    int getPreferredNetworkType(int subId);

    /**
     * Check whether DUN APN is required for tethering with subId.
     *
     * @param subId the id of the subscription to require tethering.
     * @return {@code true} if DUN APN is required for tethering.
     * @hide
     */
    boolean isTetheringApnRequiredForSubscriber(int subId);

    /**
     * Enables framework IMS and triggers IMS Registration.
     */
    void enableIms(int slotId);

    /**
     * Disables framework IMS and triggers IMS deregistration.
     */
    void disableIms(int slotId);

    /**
     * Toggle framework IMS disables and enables.
     */
    void resetIms(int slotIndex);

    /**
     *  Get IImsMmTelFeature binder from ImsResolver that corresponds to the subId and MMTel feature
     *  as well as registering the MmTelFeature for callbacks using the IImsServiceFeatureCallback
     *  interface.
     */


    /**
     *  @return true if the ImsService to bind to for the slot id specified was set, false otherwise.
     */

    /**
     *  @return true if the ImsService cleared any carrier ImsService overrides, false otherwise.
     */
    boolean clearCarrierImsServiceOverride(int slotIndex);

    /**
     * @return the package name of the carrier/device ImsService associated with this slot.
     */
    String getBoundImsServicePackage(int slotIndex, boolean isCarrierImsService, int featureType);

    /**
     * Get the MmTelFeature state attached to this subscription id.
     */

    /**
     * Set the network selection mode to automatic.
     *
     * @param subId the id of the subscription to update.
     */
    void setNetworkSelectionModeAutomatic(int subId);

    /**
     * Perform a radio scan and return the list of avialble networks.
     *
     * @param subId the id of the subscription.
     * @param callingPackage the calling package
     * @param callingFeatureId The feature in the package
     * @return CellNetworkScanResult containing status of scan and networks.
     */

    /**
     * Perform a radio network scan and return the id of this scan.
     *
     * @param subId the id of the subscription.
     * @param callingFeatureId The feature in the package
     * @return An id for this scan.
     */

    /**
     * Stop an existing radio network scan.
     *
     * @param subId the id of the subscription.
     * @param scanId The id of the scan that is going to be stopped.
     */
    void stopNetworkScan(int subId, int scanId);

    /**
     * Ask the radio to connect to the input network and change selection mode to manual.
     *
     * @param subId the id of the subscription.
     * attaching to the selected PLMN until reboot; otherwise, attach to the chosen PLMN and resume
     * normal network selection next time.
     * @return {@code true} on success; {@code true} on any failure.
     */

    /**
     * Get the allowed network types that store in the telephony provider.
     *
     * @param subId the id of the subscription.
     * @return allowedNetworkTypes the allowed network types.
     */
    long getAllowedNetworkTypes(int subId);

    /**
     * Set the allowed network types.
     *
     * @param subId the id of the subscription.
     * @param allowedNetworkTypes the allowed network types.
     * @return true on success; false on any failure.
     */
    boolean setAllowedNetworkTypes(int subId, long allowedNetworkTypes);

    /**
     * Get the allowed network types for certain reason.
     *
     * @param subId the id of the subscription.
     * @param reason the reason the allowed network type change is taking place
     * @return allowedNetworkTypes the allowed network types.
     */
    long getAllowedNetworkTypesForReason(int subId, int reason);

    /**
     * Get the effective allowed network types on the device. This API will
     * return an intersection of allowed network types for all reasons,
     * including the configuration done through setAllowedNetworkTypes
     *
     * @param subId the id of the subscription.
     * @return allowedNetworkTypes the allowed network types.
     */
    long getEffectiveAllowedNetworkTypes(int subId);

    /**
     * Set the allowed network types and provide the reason triggering the allowed network change.
     *
     * @param subId the id of the subscription.
     * @param reason the reason the allowed network type change is taking place
     * @param allowedNetworkTypes the allowed network types.
     * @return true on success; false on any failure.
     */
    boolean setAllowedNetworkTypesForReason(int subId, int reason, long allowedNetworkTypes);

    /**
     * Set the preferred network type.
     * Used for device configuration by some CDMA operators.
     *
     * @param subId the id of the subscription to update.
     * @param networkType the preferred network type, defined in RILConstants.java.
     * @return true on success; false on any failure.
     */
    boolean setPreferredNetworkType(int subId, int networkType);

    /**
     * Get the user enabled state of Mobile Data.
     *
     * TODO: remove and use isUserDataEnabled.
     * This can't be removed now because some vendor codes
     * calls through ITelephony directly while they should
     * use TelephonyManager.
     *
     * @return true on enabled
     */
    boolean getDataEnabled(int subId);

    /**
     * Get the user enabled state of Mobile Data.
     *
     * @return true on enabled
     */
    boolean isUserDataEnabled(int subId);

    /**
     * Check if data is enabled on the device. It can be disabled by
     * user, carrier, policy or thermal.
     * @return true on enabled
     */
    boolean isDataEnabled(int subId);

    /**
     * Control of data connection and provide the reason triggering the data connection control.
     *
     * @param subId user preferred subId.
     * @param reason the reason the data enable change is taking place
     * @param enable true to turn on, else false
     */
    void setDataEnabledForReason(int subId, int reason, boolean enable);

    /**
     * Return whether data is enabled for certain reason
     * @param subId user preferred subId.       .
     * @param reason the reason the data enable change is taking place
     * @return true on enabled
     */
    boolean isDataEnabledForReason(int subId, int reason);

    /**
     * Checks if manual network selection is allowed.
     *
     * @return {@code true} if manual network selection is allowed, otherwise return {@code false}.
     */
    boolean isManualNetworkSelectionAllowed(int subId);

    /**
     * Enable or disable always reporting signal strength changes from radio.
     */
    void setAlwaysReportSignalStrength(int subId, boolean isEnable);

    /**
     * Get P-CSCF address from PCO after data connection is established or modified.
     * @param apnType the apnType, "ims" for IMS APN, "emergency" for EMERGENCY APN
     * @param callingPackage The package making the call.
     * @param callingFeatureId The feature in the package.
     */
    String[] getPcscfAddress(String apnType, String callingPackage, String callingFeatureId);

    /**
     * Set IMS registration state
     */
    void setImsRegistrationState(boolean registered);

    /**
     * Return MDN string for CDMA phone.
     * @param subId user preferred subId.
     */
    String getCdmaMdn(int subId);

    /**
     * Return MIN string for CDMA phone.
     * @param subId user preferred subId.
     */
    String getCdmaMin(int subId);

    /**
     * Request that the next incoming call from a number matching {@code range} be intercepted.
     * @param callingPackage the calling package name.
     */
    /**
     * Has the calling application been granted special privileges by the carrier.
     *
     * If any of the packages in the calling UID has carrier privileges, the
     * call will return true. This access is granted by the owner of the UICC
     * card and does not depend on the registered carrier.
     *
     * TODO: Add a link to documentation.
     *
     * @param subId The subscription to use.
     * @return carrier privilege status defined in TelephonyManager.
     */
    int getCarrierPrivilegeStatus(int subId);

    /**
     * Similar to above, but check for the given uid.
     */
    int getCarrierPrivilegeStatusForUid(int subId, int uid);

    /**
     * Similar to above, but check for the package whose name is pkgName.
     */
    int checkCarrierPrivilegesForPackage(int subId, String pkgName);

    /**
     * Similar to above, but check across all phones.
     */
    int checkCarrierPrivilegesForPackageAnyPhone(String pkgName);

    /**
     * Returns list of the package names of the carrier apps that should handle the input intent
     * and have carrier privileges for the given phoneId.
     *
     * @param phoneId The phoneId on which the carrier app has carrier privileges.
     * @return list of carrier app package names that can handle the intent on phoneId.
     *         Returns null if there is an error and an empty list if there
     *         are no matching packages.
     */
    List<String> getCarrierPackageNamesForIntentAndPhone(Intent intent, int phoneId);

    /**
     * Set the line 1 phone number string and its alphatag for the current ICCID
     * for display purpose only, for example, displayed in Phone Status. It won't
     * change the actual MSISDN/MDN. To unset alphatag or number, pass in a null
     * value.
     *
     * @param subId the subscriber that the alphatag and dialing number belongs to.
     * @param alphaTag alpha-tagging of the dailing nubmer
     * @param number The dialing number
     * @return true if the operation was executed correctly.
     */
    boolean setLine1NumberForDisplayForSubscriber(int subId, String alphaTag, String number);

    /**
     * Returns the displayed dialing number string if it was set previously via
     * @param subId whose dialing number for line 1 is returned.
     * @param callingPackage The package making the call.
     * @param callingFeatureId The feature in the package.
     * @return the displayed dialing number if set, or null if not set.
     */
    String getLine1NumberForDisplay(int subId, String callingPackage, String callingFeatureId);

    /**
     * Returns the displayed alphatag of the dialing number if it was set
     *
     * @param subId whose alphatag associated with line 1 is returned.
     * @param callingPackage The package making the call.
     * @param callingFeatureId The feature in the package.
     * @return the displayed alphatag of the dialing number if set, or null if
     *         not set.
     */
    String getLine1AlphaTagForDisplay(int subId, String callingPackage, String callingFeatureId);

    /**
     * Return the set of subscriber IDs that should be considered "merged together" for data usage
     * purposes. This is commonly {@code null} to indicate no merging is required. Any returned
     * subscribers are sorted in a deterministic order.
     * <p>
     * The returned set of subscriber IDs will include the subscriber ID corresponding to this
     * TelephonyManager's subId.
     *
     * @hide
     */
    String[] getMergedSubscriberIds(int subId, String callingPackage, String callingFeatureId);

    /**
     * @hide
     */
    String[] getMergedImsisFromGroup(int subId, String callingPackage);

    /**
     * Override the operator branding for the current ICCID.
     *
     * Once set, whenever the SIM is present in the device, the service
     * provider name (SPN) and the operator name will both be replaced by the
     * brand value input. To unset the value, the same function should be
     * called with a null brand value.
     *
     * <p>Requires Permission:
     *   {@link android.Manifest.permission#MODIFY_PHONE_STATE MODIFY_PHONE_STATE}
     *  or has to be carrier app - see #hasCarrierPrivileges.
     *
     * @param subId The subscription to use.
     * @param brand The brand name to display/set.
     * @return true if the operation was executed correctly.
     */
    boolean setOperatorBrandOverride(int subId, String brand);

    /**
     * Override the roaming indicator for the current ICCID.
     *
     * Using this call, the carrier app (see #hasCarrierPrivileges) can override
     * the platform's notion of a network operator being considered roaming or not.
     * The change only affects the ICCID that was active when this call was made.
     *
     * If null is passed as any of the input, the corresponding value is deleted.
     *
     * <p>Requires that the caller have carrier privilege. See #hasCarrierPrivileges.
     *
     * @param subId for which the roaming overrides apply.
     * @return true if the operation was executed correctly.
     */
    boolean setRoamingOverride(int subId, List<String> gsmRoamingList,
                               List<String> gsmNonRoamingList, List<String> cdmaRoamingList,
                               List<String> cdmaNonRoamingList);

    /**
     * Returns the result and response from RIL for oem request
     *
     * @return negative value request was not handled or get error
     *         0 request was handled succesfully, but no response data
     *         positive value success, data length of response
     */

    /**
     * Check if any mobile Radios need to be shutdown.
     *
     * @return true is any mobile radio needs to be shutdown
     */
    boolean needMobileRadioShutdown();

    /**
     * Shutdown Mobile Radios
     */
    void shutdownMobileRadios();

    /**
     * Set phone radio type and access technology.
     *
     *        new radio access family. The length of RadioAccessFamily
     *        must equ]]al to phone count.
     */
    int getRadioAccessFamily(int phoneId, String callingPackage);

    /**
     * Enables or disables video calling.
     *
     * @param enable Whether to enable video calling.
     */
    void enableVideoCalling(boolean enable);

    /**
     * Whether video calling has been enabled by the user.
     *
     * @param callingPackage The package making the call.
     * @param callingFeatureId The feature in the package.
     * @return {@code true} if the user has enabled video calling, {@code false} otherwise.
     */
    boolean isVideoCallingEnabled(String callingPackage, String callingFeatureId);

    /**
     * Whether the DTMF tone length can be changed.
     *
     * @param subId The subscription to use.
     * @param callingPackage The package making the call.
     * @param callingFeatureId The feature in the package.
     * @return {@code true} if the DTMF tone length can be changed.
     */
    boolean canChangeDtmfToneLength(int subId, String callingPackage, String callingFeatureId);

    /**
     * Whether the device is a world phone.
     *
     * @param callingPackage The package making the call.
     * @param callingFeatureId The feature in the package.
     * @return {@code true} if the devices is a world phone.
     */
    boolean isWorldPhone(int subId, String callingPackage, String callingFeatureId);

    /**
     * Whether the phone supports TTY mode.
     *
     * @return {@code true} if the device supports TTY mode.
     */
    boolean isTtyModeSupported();

    boolean isRttSupported(int subscriptionId);

    /**
     * Whether the phone supports hearing aid compatibility.
     *
     * @return {@code true} if the device supports hearing aid compatibility.
     */
    boolean isHearingAidCompatibilitySupported();

    /**
     * Get IMS Registration Status on a particular subid.
     *
     * @param subId user preferred subId.
     *
     * @return {@code true} if the IMS status is registered.
     */
    boolean isImsRegistered(int subId);

    /**
     * Returns the Status of Wi-Fi Calling for the subscription id specified.
     */
    boolean isWifiCallingAvailable(int subId);

    /**
     * Returns the Status of VT (video telephony) for the subscription ID specified.
     */
    boolean isVideoTelephonyAvailable(int subId);

    /**
     * Returns the MMTEL IMS registration technology for the subsciption ID specified.
     */
    int getImsRegTechnologyForMmTel(int subId);

    /** @deprecated Use {@link #getDeviceIdWithFeature(String, String) instead */
    String getDeviceId(String callingPackage);

    /**
     * Returns the unique device ID of phone, for example, the IMEI for
     * GSM and the MEID for CDMA phones. Return null if device ID is not available.
     *
     * @param callingPackage The package making the call.
     * @param callingFeatureId The feature in the package
     * <p>Requires Permission:
     *   {@link android.Manifest.permission#READ_PHONE_STATE READ_PHONE_STATE}
     */
    String getDeviceIdWithFeature(String callingPackage, String callingFeatureId);

    /**
     * Returns the IMEI for the given slot.
     *
     * @param slotIndex - device slot.
     * @param callingPackage The package making the call.
     * @param callingFeatureId The feature in the package
     * <p>Requires Permission:
     *   {@link android.Manifest.permission#READ_PHONE_STATE READ_PHONE_STATE}
     */
    String getImeiForSlot(int slotIndex, String callingPackage, String callingFeatureId);

    /**
     * Returns the Type Allocation Code from the IMEI for the given slot.
     *
     * @param slotIndex - Which slot to retrieve the Type Allocation Code from.
     */
    String getTypeAllocationCodeForSlot(int slotIndex);

    /**
     * Returns the MEID for the given slot.
     *
     * @param slotIndex - device slot.
     * @param callingPackage The package making the call.
     * @param callingFeatureId The feature in the package
     * <p>Requires Permission:
     *   {@link android.Manifest.permission#READ_PHONE_STATE READ_PHONE_STATE}
     */
    String getMeidForSlot(int slotIndex, String callingPackage, String callingFeatureId);

    /**
     * Returns the Manufacturer Code from the MEID for the given slot.
     *
     * @param slotIndex - Which slot to retrieve the Manufacturer Code from.
     */
    String getManufacturerCodeForSlot(int slotIndex);

    /**
     * Returns the device software version.
     *
     * @param slotIndex - device slot.
     * @param callingPackage The package making the call.
     * @param callingFeatureId The feature in the package.
     * <p>Requires Permission:
     *   {@link android.Manifest.permission#READ_PHONE_STATE READ_PHONE_STATE}
     */
    String getDeviceSoftwareVersionForSlot(int slotIndex, String callingPackage,
                                           String callingFeatureId);

    /**
     * Returns the subscription ID associated with the specified PhoneAccount.
     */
    int getSubIdForPhoneAccount(PhoneAccount phoneAccount);

    /**
     * Returns the subscription ID associated with the specified PhoneAccountHandle.
     */
    int getSubIdForPhoneAccountHandle(PhoneAccountHandle phoneAccountHandle,
                                      String callingPackage, String callingFeatureId);

    /**
     * Returns the PhoneAccountHandle associated with a subscription ID.
     */
    PhoneAccountHandle getPhoneAccountHandleForSubscriptionId(int subscriptionId);

    void factoryReset(int subId);

    /**
     * Returns users's current locale based on the SIM.
     *
     * The returned string will be a well formed BCP-47 language tag, or {@code null}
     * if no locale could be derived.
     */
    String getSimLocaleForSubscriber(int subId);

    /**
     * Requests the modem activity info asynchronously.
     * The implementor is expected to reply with the
     * {@link android.telephony.ModemActivityInfo} object placed into the Bundle with the key
     * {@link android.telephony.TelephonyManager#MODEM_ACTIVITY_RESULT_KEY}.
     * The result code is ignored.
     */

    /**
     * Get the service state on specified subscription
     * @param subId Subscription id
     * @param callingPackage The package making the call
     * @param callingFeatureId The feature in the package
     * @return Service state on specified subscription.
     */
    ServiceState getServiceStateForSubscriber(int subId, String callingPackage,
                                              String callingFeatureId);

    /**
     * Returns the URI for the per-account voicemail ringtone set in Phone settings.
     *
     * voicemail ringtone.
     * @return The URI for the ringtone to play when receiving a voicemail from a specific
     * PhoneAccount.
     */
    Uri getVoicemailRingtoneUri(PhoneAccountHandle accountHandle);

    /**
     * Sets the per-account voicemail ringtone.
     *
     * <p>Requires that the calling app is the default dialer, or has carrier privileges, or
     * has permission {@link android.Manifest.permission#MODIFY_PHONE_STATE MODIFY_PHONE_STATE}.
     *
     * PhoneAccount.
     */
    void setVoicemailRingtoneUri(String callingPackage,
                                 PhoneAccountHandle phoneAccountHandle, Uri uri);

    /**
     * Returns whether vibration is set for voicemail notification in Phone settings.
     *
     * voicemail vibration setting.
     * @return {@code true} if the vibration is set for this PhoneAccount, {@code false} otherwise.
     */
    boolean isVoicemailVibrationEnabled(PhoneAccountHandle accountHandle);

    /**
     * Sets the per-account preference whether vibration is enabled for voicemail notifications.
     *
     * <p>Requires that the calling app is the default dialer, or has carrier privileges, or
     * has permission {@link android.Manifest.permission#MODIFY_PHONE_STATE MODIFY_PHONE_STATE}.
     *
     * voicemail vibration setting.
     * @param enabled Whether to enable or disable vibration for voicemail notifications from a
     * specific PhoneAccount.
     */
    void setVoicemailVibrationEnabled(String callingPackage,
                                      PhoneAccountHandle phoneAccountHandle, boolean enabled);

    /**
     * Returns a list of packages that have carrier privileges for the specific phone.
     */
    List<String> getPackagesWithCarrierPrivileges(int phoneId);

    /**
     * Returns a list of packages that have carrier privileges.
     */
    List<String> getPackagesWithCarrierPrivilegesForAllPhones();

    /**
     * Return the application ID for the app type.
     *
     * @param subId the subscription ID that this request applies to.
     * @param appType the uicc app type,
     * @return Application ID for specificied app type or null if no uicc or error.
     */
    String getAidForAppType(int subId, int appType);

    /**
     * Return the Electronic Serial Number.
     *
     * Requires that the calling app has READ_PRIVILEGED_PHONE_STATE permission
     *
     * @param subId the subscription ID that this request applies to.
     * @return ESN or null if error.
     * @hide
     */
    String getEsn(int subId);

    /**
     * Return the Preferred Roaming List Version
     *
     * Requires that the calling app has READ_PRIVILEGED_PHONE_STATE permission
     * @param subId the subscription ID that this request applies to.
     * @return PRLVersion or null if error.
     * @hide
     */
    String getCdmaPrlVersion(int subId);

    /**
     * Get snapshot of Telephony histograms
     * @return List of Telephony histograms
     * Requires Permission:
     *   {@link android.Manifest.permission#MODIFY_PHONE_STATE MODIFY_PHONE_STATE}
     * Or the calling app has carrier privileges.
     */

    /**
     * Set the allowed carrier list and the excluded carrier list, indicating the priority between
     * the two lists.
     *
     * <p>Requires system privileges. In the future we may add this to carrier APIs.
     *
     */
    /**
     * Returns carrier id of the given subscription.
     * <p>To recognize carrier as a first class identity, assign each carrier with a canonical
     * integer a.k.a carrier id.
     *
     * @param subId The subscription id
     * subscription is unavailable or carrier cannot be identified.
     * @throws IllegalStateException if telephony service is unavailable.
     * @hide
     */
    int getSubscriptionCarrierId(int subId);

    /**
     * Returns carrier name of the given subscription.
     * <p>Returned carrier name is unlocalized.
     *
     * @return Carrier name of given subscription id. return {@code null} if subscription is
     * unavailable or carrier cannot be identified.
     * @throws IllegalStateException if telephony service is unavailable.
     * @hide
     */
    String getSubscriptionCarrierName(int subId);

    /**
     * Returns fine-grained carrier id of the current subscription.
     *
     * <p>The specific carrier id can be used to further differentiate a carrier by different
     * networks, by prepaid v.s.postpaid or even by 4G v.s.3G plan. Each carrier has a unique
     * current underlying network.
     *
     * be identified.
     * @hide
     */
    int getSubscriptionSpecificCarrierId(int subId);

    /**
     *
     * <p>The returned name is unlocalized.
     *
     * @return user-facing name of the subscription specific carrier id. Return {@code null} if the
     * subscription is unavailable or the carrier cannot be identified.
     * @hide
     */
    String getSubscriptionSpecificCarrierName(int subId);

    /**
     * Returns carrier id based on MCCMNC only. This will return a MNO carrier id used for fallback
     * {@false} otherwise.
     *
     * @return carrier id from passing mccmnc.
     * @hide
     */
    int getCarrierIdFromMccMnc(int slotIndex, String mccmnc, boolean isSubscriptionMccMnc);

    /**
     * Action set from carrier signalling broadcast receivers to enable/disable radio
     * Permissions android.Manifest.permission.MODIFY_PHONE_STATE is required
     * @param subId the subscription ID that this action applies to.
     * @param enabled control enable or disable radio.
     * @hide
     */
    void carrierActionSetRadioEnabled(int subId, boolean enabled);

    /**
     * Action set from carrier signalling broadcast receivers to start/stop reporting default
     * network conditions.
     * Permissions android.Manifest.permission.MODIFY_PHONE_STATE is required
     * @param subId the subscription ID that this action applies to.
     * @param report control start/stop reporting default network events.
     * @hide
     */
    void carrierActionReportDefaultNetworkStatus(int subId, boolean report);

    /**
     * Action set from carrier signalling broadcast receivers to reset all carrier actions.
     * Permissions android.Manifest.permission.MODIFY_PHONE_STATE is required
     * @param subId the subscription ID that this action applies to.
     * @hide
     */
    void carrierActionResetAll(int subId);


    /**
     * Get Client request stats which will contain statistical information
     * on each request made by client.
     * @param callingPackage package making the call.
     * @param callingFeatureId The feature in the package.
     * @hide
     */

    /**
     * Set SIM card power state.
     * @param slotIndex SIM slot id
     * @param state  State of SIM (power down, power up, pass through)
     * @hide
     */
    void setSimPowerStateForSlot(int slotIndex, int state);

    /**
     * Set SIM card power state.
     * @hide
     */
    String[] getForbiddenPlmns(int subId, int appType, String callingPackage,
                               String callingFeatureId);

    /**
     * Set the forbidden PLMN list from the givven app type (ex APPTYPE_USIM) on a particular
     * subscription.
     *
     * @param subId subId the id of the subscription
     * @param appType appType the uicc app type, must be USIM or SIM.
     * @param callingPackage the op Package name.
     * @param callingFeatureId the feature in the package.
     * @return number of fplmns that is successfully written to the SIM
     */
    int setForbiddenPlmns(int subId, int appType, List<String> fplmns, String callingPackage,
                          String callingFeatureId);

    /**
     * Check if phone is in emergency callback mode
     * @return true if phone is in emergency callback mode
     * @param subId the subscription ID that this action applies to.
     * @hide
     */
    boolean getEmergencyCallbackMode(int subId);

    /**
     * Get the most recently available signal strength information.
     *
     * Get the most recent SignalStrength information reported by the modem. Due
     * to power saving this information may not always be current.
     * @param subId Subscription index
     * @return the most recent cached signal strength info from the modem
     * @hide
     */
    SignalStrength getSignalStrength(int subId);

    /**
     * Get the card ID of the default eUICC card. If there is no eUICC, returns
     *
     * @param subId subscription ID used for authentication
     * @param callingPackage package making the call
     * @return card ID of the default eUICC card.
     */
    int getCardIdForDefaultEuicc(int subId, String callingPackage);

    /**
     * Gets information about currently inserted UICCs and eUICCs.
     * <p>
     * <p>
     * If the caller has carrier priviliges on any active subscription, then they have permission to
     * get simple information like the card ID ({@link UiccCardInfo#getCardId()}), whether the card
     * is an eUICC ({@link UiccCardInfo#isEuicc()}), and the slot index where the card is inserted
     * ({@link UiccCardInfo#getSlotIndex()}).
     * <p>
     * To get private information such as the EID ({@link UiccCardInfo#getEid()}) or ICCID
     * ({@link UiccCardInfo#getIccId()}), the caller must have carrier priviliges on that specific
     * UICC or eUICC card.
     * <p>
     * See {@link UiccCardInfo} for more details on the kind of information available.
     *
     * @param callingPackage package making the call, used to evaluate carrier privileges
     * @return a list of UiccCardInfo objects, representing information on the currently inserted
     * UICCs and eUICCs. Each UiccCardInfo in the list will have private information filtered out if
     * the caller does not have adequate permissions for that card.
     */
    List<UiccCardInfo> getUiccCardsInfo(String callingPackage);

    /**
     * Get slot info for all the UICC slots.
     * @return UiccSlotInfo array.
     * @hide
     */

    /**
     * Map logicalSlot to physicalSlot, and activate the physicalSlot if it is inactive.
     *        size should be same as getPhoneCount().
     * @return boolean Return true if the switch succeeds, false if the switch fails.
     */
    boolean switchSlots(int[] physicalSlots);

    /**
     * Returns whether mobile data roaming is enabled on the subscription with id {@code subId}.
     *
     * @param subId the subscription id
     * @return {@code true} if the data roaming is enabled on this subscription.
     */
    boolean isDataRoamingEnabled(int subId);

    /**
     * Enables/Disables the data roaming on the subscription with id {@code subId}.
     *
     * @param subId the subscription id
     * @param isEnabled {@code true} to enable mobile data roaming, otherwise disable it.
     */
    void setDataRoamingEnabled(int subId, boolean isEnabled);

    /**
     * Gets the roaming mode for the CDMA phone with the subscription id {@code subId}.
     *
     * @return the roaming mode for CDMA phone.
     */
    int getCdmaRoamingMode(int subId);

    /**
     * Sets the roaming mode on the CDMA phone with the subscription {@code subId} to the given
     * roaming mode {@code mode}.
     *
     * @param subId the subscription id.
     * @param mode the roaming mode should be set.
     * @return {@code true} if successed.
     */
    boolean setCdmaRoamingMode(int subId, int mode);

    /**
     * Gets the subscription mode for the CDMA phone with the subscription id {@code subId}.
     *
     * @return the subscription mode for CDMA phone.
     */
    int getCdmaSubscriptionMode(int subId);

    /**
     * Sets the subscription mode for CDMA phone with the subscription {@code subId} to the given
     * subscription mode {@code mode}.
     *
     * @param subId the subscription id.
     * @param mode the subscription mode should be set.
     * @return {@code true} if successed.
     */
    boolean setCdmaSubscriptionMode(int subId, int mode);

    /**
     * A test API to override carrier information including mccmnc, imsi, iccid, gid1, gid2,
     * plmn and spn. This would be handy for, eg, forcing a particular carrier id, carrier's config
     * (also any country or carrier overlays) to be loaded when using a test SIM with a call box.
     */
    void setCarrierTestOverride(int subId, String mccmnc, String imsi, String iccid, String gid1,
                                String gid2, String plmn, String spn, String carrierPrivilegeRules, String apn);

    /**
     * A test API to return installed carrier id list version.
     */
    int getCarrierIdListVersion(int subId);

    /**
     * A test API to reload the UICC profile.
     * @hide
     */
    void refreshUiccProfile(int subId);

    /**
     * How many modems can have simultaneous data connections.
     * @hide
     */
    int getNumberOfModemsWithSimultaneousDataConnections(int subId, String callingPackage,
                                                         String callingFeatureId);

    /**
     * Return the network selection mode on the subscription with id {@code subId}.
     */
    int getNetworkSelectionMode(int subId);

    /**
     * Return true if the device is in emergency sms mode, false otherwise.
     */
    boolean isInEmergencySmsMode();

    /**
     * Return the modem radio power state for slot index.
     *
     */
    int getRadioPowerState(int slotIndex, String callingPackage, String callingFeatureId);

    // IMS specific AIDL commands, see ImsMmTelManager.java

    /**
     * Adds an IMS registration status callback for the subscription id specified.
     */
    /**
     * Removes an existing IMS registration status callback for the subscription specified.
     */

    /**
     * Get the IMS service registration state for the MmTelFeature associated with this sub id.
     */

    /**
     * Get the transport type for the IMS service registration state.
     */

    /**
     * Adds an IMS MmTel capabilities callback for the subscription specified.
     */

    /**
     * return true if the IMS MmTel capability for the given registration tech is capable.
     */
    boolean isCapable(int subId, int capability, int regTech);

    /**
     * return true if the IMS MmTel capability for the given registration tech is available.
     */
    boolean isAvailable(int subId, int capability, int regTech);

    /**
     * Return whether or not the MmTel capability is supported for the requested transport type.
     */

    /**
     * Returns true if the user's setting for 4G LTE is enabled, for the subscription specified.
     */
    boolean isAdvancedCallingSettingEnabled(int subId);

    /**
     * Modify the user's setting for whether or not 4G LTE is enabled.
     */
    void setAdvancedCallingSettingEnabled(int subId, boolean isEnabled);

    /**
     * return true if the user's setting for VT is enabled for the subscription.
     */
    boolean isVtSettingEnabled(int subId);

    /**
     * Modify the user's setting for whether or not VT is available for the subscrption specified.
     */
    void setVtSettingEnabled(int subId, boolean isEnabled);

    /**
     * return true if the user's setting for whether or not Voice over WiFi is currently enabled.
     */
    boolean isVoWiFiSettingEnabled(int subId);

    /**
     * sets the user's setting for Voice over WiFi enabled state.
     */
    void setVoWiFiSettingEnabled(int subId, boolean isEnabled);

    /**
     * return true if the user's setting for Voice over WiFi while roaming is enabled.
     */
    boolean isVoWiFiRoamingSettingEnabled(int subId);

    /**
     * Sets the user's preference for whether or not Voice over WiFi is enabled for the current
     * subscription while roaming.
     */
    void setVoWiFiRoamingSettingEnabled(int subId, boolean isEnabled);

    /**
     * Set the Voice over WiFi enabled state, but do not persist the setting.
     */
    void setVoWiFiNonPersistent(int subId, boolean isCapable, int mode);

    /**
     * return the Voice over WiFi mode preference set by the user for the subscription specified.
     */
    int getVoWiFiModeSetting(int subId);

    /**
     * sets the user's preference for the Voice over WiFi mode for the subscription specified.
     */
    void setVoWiFiModeSetting(int subId, int mode);

    /**
     * return the Voice over WiFi mode preference set by the user for the subscription specified
     * while roaming.
     */
    int getVoWiFiRoamingModeSetting(int subId);

    /**
     * sets the user's preference for the Voice over WiFi mode for the subscription specified
     * while roaming.
     */
    void setVoWiFiRoamingModeSetting(int subId, int mode);

    /**
     * Modify the user's setting for whether or not RTT is enabled for the subscrption specified.
     */
    void setRttCapabilitySetting(int subId, boolean isEnabled);

    /**
     * return true if TTY over VoLTE is enabled for the subscription specified.
     */
    boolean isTtyOverVolteEnabled(int subId);

    /**
     * Return the emergency number list from all the active subscriptions.
     */
    Map getEmergencyNumberList(String callingPackage, String callingFeatureId);

    /**
     * Identify if the number is emergency number, based on all the active subscriptions.
     */
    boolean isEmergencyNumber(String number, boolean exactMatch);

    /**
     * Return a list of certs in hex string from loaded carrier privileges access rules.
     */
    List<String> getCertsFromCarrierPrivilegeAccessRules(int subId);

    /**
     * Register an IMS provisioning change callback with Telephony.
     */

    /**
     * Set the provisioning status for the IMS MmTel capability using the specified subscription.
     */
    void setImsProvisioningStatusForCapability(int subId, int capability, int tech,
                                               boolean isProvisioned);

    /**
     * Get the provisioning status for the IMS MmTel capability specified.
     */
    boolean getImsProvisioningStatusForCapability(int subId, int capability, int tech);

    /**
     * Get the provisioning status for the IMS Rcs capability specified.
     */
    boolean getRcsProvisioningStatusForCapability(int subId, int capability);

    /**
     * Set the provisioning status for the IMS Rcs capability using the specified subscription.
     */
    void setRcsProvisioningStatusForCapability(int subId, int capability,
                                               boolean isProvisioned);

    /** Is the capability and tech flagged as provisioned in the cache */
    boolean isMmTelCapabilityProvisionedInCache(int subId, int capability, int tech);

    /** Set the provisioning for the capability and tech in the cache */
    void cacheMmTelCapabilityProvisioning(int subId, int capability, int tech,
                                          boolean isProvisioned);

    /**
     * Return an integer containing the provisioning value for the specified provisioning key.
     */
    int getImsProvisioningInt(int subId, int key);

    /**
     * return a String containing the provisioning value for the provisioning key specified.
     */
    String getImsProvisioningString(int subId, int key);

    /**
     * Set the integer provisioning value for the provisioning key specified.
     */
    int setImsProvisioningInt(int subId, int key, int value);

    /**
     * Set the String provisioning value for the provisioning key specified.
     */
    int setImsProvisioningString(int subId, int key, String value);

    /**
     * Update Emergency Number List for Test Mode.
     */
    void updateEmergencyNumberListTestMode(int action, EmergencyNumber num);

    /**
     * Get the full emergency number list for Test Mode.
     */
    List<String> getEmergencyNumberListTestMode();

    /**
     * A test API to return the emergency number db version.
     */
    int getEmergencyNumberDbVersion(int subId);

    /**
     * Notify Telephony for OTA emergency number database installation complete.
     */
    void notifyOtaEmergencyNumberDbInstalled();

    /**
     * Override a customized file partition name for OTA emergency number database.
     */

    /**
     * Reset file partition to default for OTA emergency number database.
     */
    void resetOtaEmergencyNumberDbFilePath();

    /**
     * Enable or disable a logical modem stack associated with the slotIndex.
     */
    boolean enableModemForSlot(int slotIndex, boolean enable);

    /**
     * Indicate if the enablement of multi SIM functionality is restricted.
     * @hide
     */
    void setMultiSimCarrierRestriction(boolean isMultiSimCarrierRestricted);

    /**
     * Returns if the usage of multiple SIM cards at the same time is supported.
     *
     * @param callingPackage The package making the call.
     * @param callingFeatureId The feature in the package.
     * functionality is restricted by the carrier.
     */
    int isMultiSimSupported(String callingPackage, String callingFeatureId);

    /**
     * Switch configs to enable multi-sim or switch back to single-sim
     * @hide
     */
    void switchMultiSimConfig(int numOfSims);

    /**
     * Get if altering modems configurations will trigger reboot.
     * @hide
     */
    boolean doesSwitchMultiSimConfigTriggerReboot(int subId, String callingPackage,
                                                  String callingFeatureId);

    /**
     * Get the mapping from logical slots to physical slots.
     */
    int[] getSlotsMapping();

    /**
     * Get the IRadio HAL Version encoded as 100 * MAJOR_VERSION + MINOR_VERSION or -1 if unknown
     */
    int getRadioHalVersion();

    /**
     * Get the current calling package name.
     */
    String getCurrentPackageName();

    /**
     * on the UICC card.
     * @hide
     */
    boolean isApplicationOnUicc(int subId, int appType);

    boolean isModemEnabledForSlot(int slotIndex, String callingPackage, String callingFeatureId);

    boolean isDataEnabledForApn(int apnType, int subId, String callingPackage);

    boolean isApnMetered(int apnType, int subId);


    List<RadioAccessSpecifier> getSystemSelectionChannels(int subId);

    boolean isMvnoMatched(int subId, int mvnoType, String mvnoMatchData);

    /**
     * Enqueue a pending sms Consumer, which will answer with the user specified selection for an
     * outgoing SmsManager operation.
     */

    /**
     * Returns the MMS user agent.
     */
    String getMmsUserAgent(int subId);

    /**
     * Returns the MMS user agent profile URL.
     */
    String getMmsUAProfUrl(int subId);

    void setMobileDataPolicyEnabledStatus(int subscriptionId, int policy, boolean enabled);

    boolean isMobileDataPolicyEnabled(int subscriptionId, int policy);

    /**
     * Command line command to enable or disable handling of CEP data for test purposes.
     */

    /**
     * Notify Rcs auto config received.
     */
    void notifyRcsAutoConfigurationReceived(int subId, byte[] config, boolean isCompressed);

    boolean isIccLockEnabled(int subId);

    int setIccLockEnabled(int subId, boolean enabled, String password);

    int changeIccLockPassword(int subId, String oldPassword, String newPassword);

    /**
     * Request for receiving user activity notification
     */

    /**
     * Called when userActivity is signalled in the power manager.
     * This is safe to call from any thread, with any window manager locks held or not.
     */

    /**
     * Get the user manual network selection.
     * Return empty string if in automatic selection.
     *
     * @param subId the id of the subscription
     * @return operatorinfo on success
     */
    String getManualNetworkSelectionPlmn(int subId);

    /**
     * Whether device can connect to 5G network when two SIMs are active.
     */
    boolean canConnectTo5GInDsdsMode();

    /**
     * Returns a list of the equivalent home PLMNs (EF_EHPLMN) from the USIM app.
     *
     * @return A list of equivalent home PLMNs. Returns an empty list if EF_EHPLMN is empty or
     * does not exist on the SIM card.
     */
    List<String> getEquivalentHomePlmns(int subId, String callingPackage, String callingFeatureId);

    /**
     * Enable/Disable E-UTRA-NR Dual Connectivity
     * @return operation result. See TelephonyManager.EnableNrDualConnectivityResult for
     * details
     * @param subId the id of the subscription
     */
    int setNrDualConnectivityState(int subId, int nrDualConnectivityState);

    /**
     * Is E-UTRA-NR Dual Connectivity enabled
     * @param subId the id of the subscription
     * @return true if dual connectivity is enabled else false
     */
    boolean isNrDualConnectivityEnabled(int subId);

    /**
     * Get carrier bandwidth per primary and secondary carrier
     * @return CarrierBandwidth with bandwidth of both primary and secondary carrier.
     */

    /**
     * Thermal mitigation request to control functionalities at modem.
     *
     * @param subId the id of the subscription
     */

    /**
     * Get the Generic Bootstrapping Architecture authentication keys
     */

    /**
     * Set the GbaService Package Name that Telephony will bind to.
     */
    boolean setBoundGbaServiceOverride(int subId, String packageName);

    /**
     * Return the package name of the currently bound GbaService.
     */
    String getBoundGbaService(int subId);

    /**
     * Set the release time for telephony to unbind GbaService.
     */
    boolean setGbaReleaseTimeOverride(int subId, int interval);

    /**
     * Return the release time for telephony to unbind GbaService.
     */
    int getGbaReleaseTime(int subId);
}
