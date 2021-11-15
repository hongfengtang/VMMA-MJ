### 一般取藥流程：
1. 點選病床. 
2. 點選醫囑號
`2.1 根據病例號和病人ID，返回醫囑`
3. 调用provide/basic，(傳入 ChartNo, PhrOrderNo...等）拿到通道号，药品数量, 是否取藥完成(IsProvided) 
4. 调用medicine，拿到藥品詳細信息 (圖檔, 說明, 等)
4. 取药 (僅取出 IsProvided = false 的藥品)
5. 调用result接口回写

### 临时取药 (QRCode) 流程：
#### 二维码格式范例：
	A389159:q2021060301**005865:1;005886:1;023880:1;
	病历号:医嘱号**药品ID:数量;药品ID:数量;药品ID:数量;

1. 扫二维码
2. 调用provide/basic，(ChartNo, PhrOrderNo...等）拿到通道号，药品数, 是否取藥完成(IsProvided)
3. 调用medicine，拿到藥品詳細信息 (圖檔, 說明, 等)
4. 取药 (僅取出 IsProvided = false 的藥品)
5. 调用result接口回写

`一般取藥與临时取药過程中如果其中有失敗時, 調用result接口時僅回傳取藥成功的藥品, 下次執行時, 就只取沒有成功的藥品即可`

### 緊急取藥
1. 點選病床. 
2. 點選要取出的藥品
3. 调用provide/basic，(傳入 ChartNo, PhrOrderNo=”999999”）拿到系統自動產生的 OrderSN (tYYYYMMDDHHmm-<TeminalId>), 通道号，药品数量, 是否取藥完成(IsProvided) 
4. 调用medicine，拿到藥品詳細信息 (圖檔, 說明, 等)
4. 取药 (僅取出 IsProvided = false 的藥品)
5. 调用result接口回写

` 統一 provide 接口, 根據傳入的 PhrOrderNo 來判斷是哪一種類型的取藥`
`取消 provide/batch 接口`
 

PhrOrderNo:
 p 開頭: 一般 (醫囑號) 取藥, 
q 開頭: 臨時 (QRCode) 取藥, 
空白: 緊急取藥 ( 因 QRCode 取藥有 PhrOrderNo, 所以使用緊急取藥改送空白即可, 不用特別用 999999)