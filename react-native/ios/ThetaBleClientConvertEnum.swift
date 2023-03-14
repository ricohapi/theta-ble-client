//
//  ThetaBleClientConvertEnum.swift
//  ThetaBleClientReactNative
//
//  Created on 2023/03/20.
//

import Foundation
import THETABleClient

func getEnumValue<T, E: KotlinEnum<T>>(values: KotlinArray<E>, name: String) -> E? {
    for i in 0..<values.size {
        let item = values.get(index: i)!
        if item.name == name {
            return item
        }
    }
    return nil
}
