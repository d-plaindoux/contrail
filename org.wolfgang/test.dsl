<?xml version="1.0"?>
<Components>
<Definitions>
<Transducer id="trans:payload" type="PayLoad"/>
<Transducer id="trans:serial" type="Serializer"/>
</Definitions>
<Providers>
<Initial up="byte[]" down="byte[]">
   <Component refid="trans:payload"/>
   <Component refid="trans:serial"/>  
</Initial>
</Providers>
</Components>


<!-- 
   initial { router | [ service | transfer ] | }
   when byte[] * byte[] { new entry | new trans:payload | new trans:serial | router }
-->