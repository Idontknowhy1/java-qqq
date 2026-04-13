import CryptoJS from "crypto-js";

const key: string = "46EBA22EF5204DD5B110A1F730513965";

/**
 * 将字符串加密为Base64格式的字符串（适用于前端环境）
 * @param content 待加密的明文
 * @param encryptKey 加密密钥（字符串）
 * @returns 加密后的Base64格式字符串
 */
export const aes_encrypt = (
  content: string,
  encryptKey: string = key,
): string => {
  // 将密钥转换为WordArray格式[1,5](@ref)
  const key: CryptoJS.lib.WordArray = CryptoJS.enc.Utf8.parse(encryptKey);

  // 将明文内容转换为WordArray[1](@ref)
  const contentWordArray: CryptoJS.lib.WordArray =
    CryptoJS.enc.Utf8.parse(content);

  // 执行AES加密，使用ECB模式和PKCS7填充[3,5](@ref)
  const encrypted: CryptoJS.lib.CipherParams = CryptoJS.AES.encrypt(
    contentWordArray,
    key,
    {
      mode: CryptoJS.mode.ECB,
      padding: CryptoJS.pad.Pkcs7,
    },
  );

  // 返回Base64格式的字符串[1,5](@ref)
  return encrypted.toString();
};

/**
 * 将Base64格式的加密字符串解密为原始字符串
 * @param encryptedData 加密后的Base64格式字符串
 * @param decryptKey 解密密钥（字符串）
 * @returns 解密后的原始字符串
 */
export const aes_decrypt = (
  encryptedData: string,
  decryptKey: string = key,
): string => {
  // 将密钥转换为WordArray格式[1,5](@ref)
  const key: CryptoJS.lib.WordArray = CryptoJS.enc.Utf8.parse(decryptKey);

  // 执行AES解密[1,3](@ref)
  const decrypted: CryptoJS.lib.WordArray = CryptoJS.AES.decrypt(
    encryptedData,
    key,
    {
      mode: CryptoJS.mode.ECB,
      padding: CryptoJS.pad.Pkcs7,
    },
  );

  // 将解密结果转换为UTF-8字符串[1,4](@ref)
  return decrypted.toString(CryptoJS.enc.Utf8);
};
