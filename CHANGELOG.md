# 2021-06-06 
1. 修改三個場景取藥接口，統一到provide/basic
2. 臨時取藥二維碼增加醫囑部份， 例如：


	A389159:q2021060301**005865:1;005886:1;023880:1;
---

## 待修改的部分：
###一般取藥
__說明：__
1. 病人ID与病历号1:1
2. 病人ID或病历号与医嘱号1:N
3. 是否取药绑定在医嘱
4. 一般取药通过provide/basic返回的都是未取药内容，前端不需要做任何判断，不需要理会IsProvided字段

__修改部份__
1. 增加medicinedata字段，送空数组


###临时取药(改动最大)
1. QRCode数据用于上送，要取多少药由中控返回
2. 完全由中控控制


###紧急取药：
1. result要回传orderSN


