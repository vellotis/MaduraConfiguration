/*******************************************************************************
 * Copyright (c)2013 Prometheus Consulting
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package nz.co.senanque.madura.configuration;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration.beanutils.BeanDeclaration;
import org.apache.commons.configuration.beanutils.DefaultBeanFactory;
import org.apache.commons.configuration.beanutils.XMLBeanDeclaration;
import org.apache.commons.configuration.tree.ConfigurationNode;

/**
 * 
 * This is a factory for apache.commons.config.
 * You can configure it to return any class that takes setters like this:<br/>
 * <code>
      &lt;test2 config-class="nz.co.senanque.madura.configuration.MyTestBean"<br/>
            config-factory="nz.co.senanque.madura.configuration.SetterBeanFactory" a="XYZ" b="ABC"/&gt;
 * </code>
 * <br/>
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.4 $
 */
public class SetterBeanFactory extends DefaultBeanFactory
{
    /** A map for the so far created instances.*/
    private Map<String,Object> beans;
    
    public SetterBeanFactory()
    {
        super();
        beans = new HashMap<String,Object>();
    }
    
    // Creates the bean. Checks if already an instance exists.
    public synchronized Object createBean(Class beanClass, BeanDeclaration decl,
        Object param) throws Exception
    {
        Map<String,String> m = decl.getBeanProperties();
        XMLBeanDeclaration xmlDecl = (XMLBeanDeclaration)decl;
        ConfigurationNode n = xmlDecl.getNode();
        String nodeName = n.getName();
        n = n.getParentNode();
        while (n != null)
        {
            
            if (n.getName() != null)
                nodeName = n.getName()+"/"+nodeName;
            n = n.getParentNode();
        }
        
        Object bean = beans.get(nodeName);
        if (bean != null)
        {
            // Yes, there is already an instance
            return bean;
        }
        else
        {
            // create the bean and use the rest of the attributes as properties
            bean = beanClass.newInstance();
            for (Map.Entry entry: m.entrySet())
            {
                String key = (String)entry.getKey();
                if (key.equals("config-class")) continue;
                org.apache.commons.beanutils.BeanUtils.setProperty(bean,key,entry.getValue());
            }
            // Store it in map
            beans.put(nodeName, bean);
            return bean;
        }
    }
}
