# Debug Product Flow - Kiểm tra luồng dữ liệu

## Vấn đề hiện tại
ProductDetailFragment không hiển thị đúng thông tin sản phẩm

## Các cải thiện đã thực hiện

### 1. ProductDetailFragment
- ✅ Thêm method `newInstance(ProductEntity product)` để truyền thông tin đầy đủ
- ✅ Hiển thị thông tin cơ bản ngay lập tức từ arguments
- ✅ Fallback với dữ liệu test khi không có dữ liệu từ server
- ✅ Cải thiện error handling và logging

### 2. HomeFragment & AllProductsFragment
- ✅ Cập nhật để sử dụng `ProductDetailFragment.newInstance(product)`
- ✅ Truyền thông tin sản phẩm đầy đủ thay vì chỉ ID

### 3. ProductRepository
- ✅ Thêm debug logging chi tiết cho từng field
- ✅ Log document data từ Firebase
- ✅ Đảm bảo product ID được set đúng

## Cách test

### Test 1: Với dữ liệu từ Firebase
```bash
# Xem logs
adb logcat | grep -E "(ProductDetailFragment|ProductRepository|HomeFragment|AllProductsFragment)"
```

**Expected logs:**
```
ProductRepository: Starting to read data from Firestore...
ProductRepository: Firestore query successful. Documents count: X
ProductRepository: Document data: {name=..., price=..., imageURL=...}
ProductRepository: Product created: ID=..., Name=..., Price=...
HomeFragment: Product clicked: [product_name]
ProductDetailFragment: Requested Product ID: [id]
ProductDetailFragment: Displaying basic info
ProductDetailFragment: Data received from ViewModel. Products count: X
ProductDetailFragment: Found product: [name]
ProductDetailFragment: Product details displayed successfully
```

### Test 2: Với dữ liệu test (fallback)
```bash
# Tắt internet hoặc giả lập lỗi Firebase
```

**Expected logs:**
```
ProductDetailFragment: Requested Product ID: [id]
ProductDetailFragment: Displaying basic info
ProductDetailFragment: Data received from ViewModel. Products count: 0
ProductDetailFragment: No products available, showing test data
ProductDetailFragment: Displaying test data
```

## Debug Commands

### Xem logs real-time:
```bash
adb logcat | grep -E "(ProductDetailFragment|ProductRepository)"
```

### Clear app data:
```bash
adb shell pm clear com.example.tuibikho
```

### Restart app:
```bash
adb shell am force-stop com.example.tuibikho
adb shell am start -n com.example.tuibikho/.MainActivity
```

### Test specific scenario:
```bash
# Test với internet
adb shell svc wifi enable

# Test không có internet
adb shell svc wifi disable
```

## Các vấn đề có thể gặp

### 1. Firebase Connection
- **Symptom**: Không có logs từ ProductRepository
- **Solution**: Kiểm tra internet và Firebase config

### 2. Data Mapping
- **Symptom**: Product fields null hoặc empty
- **Solution**: Kiểm tra Firebase document structure

### 3. ViewModel Sharing
- **Symptom**: Data không được observe
- **Solution**: Đảm bảo ViewModel được share đúng

### 4. Fragment Lifecycle
- **Symptom**: Fragment không hiển thị
- **Solution**: Kiểm tra FragmentTransaction

## Expected Behavior

### Khi có dữ liệu từ Firebase:
1. Fragment hiển thị thông tin cơ bản ngay lập tức
2. Sau đó load thông tin chi tiết từ Firebase
3. Cập nhật UI với dữ liệu đầy đủ

### Khi không có dữ liệu từ Firebase:
1. Fragment hiển thị thông tin cơ bản
2. Hiển thị dữ liệu test cho các field còn lại
3. Toast message thông báo

### Khi có lỗi:
1. Fragment không crash
2. Hiển thị thông tin có sẵn
3. Error message phù hợp

## Test Cases

### Test Case 1: Normal Flow
- **Action**: Click sản phẩm từ HomeFragment
- **Expected**: Hiển thị đúng thông tin sản phẩm
- **Actual**: [Ghi kết quả]

### Test Case 2: AllProductsFragment
- **Action**: Click sản phẩm từ AllProductsFragment
- **Expected**: Hiển thị đúng thông tin sản phẩm
- **Actual**: [Ghi kết quả]

### Test Case 3: No Internet
- **Action**: Tắt internet, click sản phẩm
- **Expected**: Hiển thị thông tin cơ bản + test data
- **Actual**: [Ghi kết quả]

### Test Case 4: Back Navigation
- **Action**: Click back button
- **Expected**: Quay lại màn hình trước
- **Actual**: [Ghi kết quả]

## Next Steps

1. **Test với dữ liệu thực từ Firebase**
2. **Kiểm tra document structure trong Firebase**
3. **Verify ViewModel sharing giữa fragments**
4. **Test với different product types**
5. **Implement error recovery mechanisms** 