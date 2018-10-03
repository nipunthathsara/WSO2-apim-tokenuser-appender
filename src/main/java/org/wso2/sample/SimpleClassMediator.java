package org.wso2.sample;

import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.synapse.MessageContext;
import org.apache.synapse.mediators.AbstractMediator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.sample.util.ConfigReader;

import javax.xml.stream.XMLStreamException;

public class SimpleClassMediator extends AbstractMediator {
    private static final Log log = LogFactory.getLog(SimpleClassMediator.class);
    private static final String USER_NAME = "user_name";
    private static final String REMOVE_TENANT_DOMAIN = "removeTenantDomain";
    private ConfigReader configReader = null;
    private boolean removeTenantDomain = false;
    private OMFactory omFactory = OMAbstractFactory.getOMFactory();

    public SimpleClassMediator() {
        configReader = new ConfigReader();
        if ("true".equals(configReader.getProperty(REMOVE_TENANT_DOMAIN))) {
            removeTenantDomain = true;
        } else {
            removeTenantDomain = false;
        }
    }

    public boolean mediate(MessageContext mc) {
        String userName = null;
        if (mc.getProperty("api.ut.userName") != null && (mc.getProperty("api.ut.userName") instanceof String)) {
            userName = (String) mc.getProperty("api.ut.userName");
        }
        try {
            if (removeTenantDomain) {
                mc.setProperty(USER_NAME, buildPropertyValue(removeTenantDomain(userName)));
            } else {
                mc.setProperty(USER_NAME, buildPropertyValue(userName));
            }
        } catch (XMLStreamException e) {
            log.error(e.getMessage(), e);
        }
        return true;
    }

    public String removeTenantDomain(String userName) {
        // Get the substring before the last '@' sign
        // ex : johndoe@carbon.super -> johndoe
        int lastIndx = userName.lastIndexOf('@');
        return userName.substring(0, lastIndx);
    }

    /**
     * This method builds the property value from the user name passed.
     * Sample synapse property - user_name : <dat:username>johndoe</dat:username>
     *
     * @param userName
     * @return
     */
    public OMElement buildPropertyValue(String userName) throws XMLStreamException {
        String tagName = "userName";
        OMNamespace dat = omFactory.createOMNamespace("http://ws.wso2.org/dataservice", "dat");
        OMElement userNameTag = omFactory.createOMElement(tagName, dat);
        userNameTag.setText(userName);
        return userNameTag;
    }
}
