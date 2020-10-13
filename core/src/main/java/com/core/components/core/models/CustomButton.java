package com.core.components.core.models;

import com.adobe.cq.wcm.core.components.models.Button;
import lombok.experimental.Delegate;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.models.annotations.via.ResourceSuperType;

import javax.annotation.Resource;

@Model(
        adaptables = {Resource.class, SlingHttpServletRequest.class},
        adapters = Button.class, // Adapts to the CC model interface
        resourceType = "core-components/components/button", // Maps to OUR component, not the CC component
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL // No properties? No problem!
)
public class CustomButton implements Button {

    @Self // Indicates that we are resolving the current resource
    @Via(type = ResourceSuperType.class) // Resolve not as this model, but as the model of our supertype (ie: CC Button)
    @Delegate(excludes = DelegationExclusion.class) // Delegate all our methods to the CC Button except those defined below
    private Button delegate;

    @ValueMapValue
    private String target; // This is a new property that we are introducing


    public String getTarget() {
        return target;
    }

    private interface DelegationExclusion { // Here we define the methods we want to override
        String getTarget(); // Override the method which determines the target  of the link
    }

}
