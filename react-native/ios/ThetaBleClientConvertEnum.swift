//
//  ThetaBleClientConvertEnum.swift
//  ThetaBleClientReactNative
//
//  Created on 2023/03/20.
//

import Foundation
import THETABleClient

func getEnumValue<T, E: KotlinEnum<T>>(values: KotlinArray<E>, name: Any) -> E? {
    guard let strName = name as? String else {
        return nil
    }
    for i in 0 ..< values.size {
        let item = values.get(index: i)!
        if item.name == strName {
            return item
        }
    }
    return nil
}
